package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.StreamedContent;

import ubc.pavlab.ndb.beans.services.CacheService;
import ubc.pavlab.ndb.beans.services.StatsService;
import ubc.pavlab.ndb.beans.services.VariantService;
import ubc.pavlab.ndb.model.Event;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.model.Variant;
import ubc.pavlab.ndb.utility.CSVExporter;

@ManagedBean
@ViewScoped
public class VariantView implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -506601459390058684L;

    private static final Logger log = Logger.getLogger( VariantView.class );

    private static final int LAZY_LOAD_MAX_SIZE = 50;

    private String query;

    @ManagedProperty("#{variantService}")
    private VariantService variantService;

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    @ManagedProperty("#{statsService}")
    private StatsService statsService;

    private List<Variant> variants;

    // If variant size > LAZY_LOAD_MAX_SIZE
    private boolean liveScroll;

    private List<Event> events;
    private boolean complexVariant = false;

    private Variant selectedVariant;

    private Paper selectedPaper;
    // private Integer selectedPaperVariantCnt;
    private Integer selectedPaperEventCnt;

    private CSVExporter csvExporter;

    @PostConstruct
    public void init() {
        if ( FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest() ) {
            return; // Skip ajax requests.
        }
        log.info( "init" );
        Map<String, String> requestParams = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();

        String ncbiGeneId = requestParams.get( "NCBIGeneId" );
        String chr = requestParams.get( "chr" );
        String start = requestParams.get( "start" );
        String stop = requestParams.get( "stop" );
        String paperIdParam = requestParams.get( "paperId" );
        String overlapPaperIdParam = requestParams.get( "overlapPaperId" );
        if ( !StringUtils.isBlank( ncbiGeneId ) ) {
            // Search by Gene
            try {
                Integer geneId = Integer.parseInt( ncbiGeneId );
                this.query = cacheService.getGeneById( geneId ).getSymbol();
                this.variants = this.variantService.fetchByGeneId( geneId );
            } catch ( NumberFormatException | NullPointerException e ) {
                throw new IllegalArgumentException( "Malformed Search Parameters" );
            }

        } else if ( !StringUtils.isBlank( paperIdParam ) ) {
            // Search by Paper
            try {
                Integer paperId = Integer.parseInt( paperIdParam );

                if ( StringUtils.isBlank( overlapPaperIdParam ) ) {
                    this.query = cacheService.getPaperById( paperId ).getPaperKey();
                    this.variants = this.variantService.fetchByPaperId( paperId );
                } else {
                    Integer overlapPaperId = Integer.parseInt( overlapPaperIdParam );
                    if ( overlapPaperId == paperId ) {
                        this.query = cacheService.getPaperById( paperId ).getPaperKey();
                        this.variants = this.variantService.fetchByPaperId( paperId );
                    } else {
                        this.query = cacheService.getPaperById( paperId ).getPaperKey() + " Overlap With "
                                + cacheService.getPaperById( overlapPaperId ).getPaperKey();
                        this.variants = this.variantService.fetchByPaperOverlap( paperId, overlapPaperId );
                    }
                }

            } catch ( NumberFormatException | NullPointerException e ) {
                throw new IllegalArgumentException( "Malformed Search Parameters" );
            }

        } else if ( !StringUtils.isBlank( chr ) && !StringUtils.isBlank( start ) && !StringUtils.isBlank( stop ) ) {
            // Search by coordinates
            try {
                Integer startCoord = Integer.parseInt( start );
                Integer stopCoord = Integer.parseInt( stop );
                this.query = chr + ":" + start + "-" + stop;
                this.variants = this.variantService.fetchByPosition( chr, startCoord, stopCoord );
            } catch ( NumberFormatException e ) {
                throw new IllegalArgumentException( "Malformed Search Parameters" );
            }
        } else {
            // Unknown Search
            throw new IllegalArgumentException( "Unknown Search Parameters" );
        }

        this.events = Event.groupVariants( this.variants );

        this.liveScroll = ( this.events.size() > LAZY_LOAD_MAX_SIZE );

        for ( Event event : events ) {
            complexVariant |= event.isComplex();
        }

        csvExporter = new CSVExporter( "variants.csv" );

        log.info( "Variants: " + this.variants.size() );
        log.info( "Events: " + this.events.size() );

    }

    public StreamedContent getCsv() {
        if ( !csvExporter.hasData() ) {
            csvExporter.loadData( events );
        }
        return csvExporter.getStreamedContent();
    }

    public String getQuery() {
        return query;
    }

    public Variant getSelectedVariant() {
        return selectedVariant;
    }

    public void setSelectedVariant( Variant trunk ) {
        this.selectedVariant = trunk;
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public List<Event> getEvents() {
        return events;
    }

    public boolean isComplexVariant() {
        return complexVariant;
    }

    public Paper getSelectedPaper() {
        return selectedPaper;
    }

    public boolean isLiveScroll() {
        return liveScroll;
    }

    public void setSelectedPaper( Paper selectedPaper ) {
        // selectedPaperVariantCnt = statsService.getVariantCntByPaperId( selectedPaper.getId() );
        selectedPaperEventCnt = statsService.getEventCntByPaperId( selectedPaper.getId() );
        this.selectedPaper = selectedPaper;
    }

    // public Integer getSelectedPaperVariantCnt() {
    // return selectedPaperVariantCnt;
    // }

    public Integer getSelectedPaperEventCnt() {
        return selectedPaperEventCnt;
    }

    public int sortByGenes( Object e1, Object e2 ) {
        return Event.COMPARE_GENES.compare( ( Event ) e1, ( Event ) e2 );
    }

    public int sortByEffects( Object e1, Object e2 ) {
        return Event.COMPARE_EFFECTS.compare( ( Event ) e1, ( Event ) e2 );
    }

    public int sortByPapers( Object e1, Object e2 ) {
        return Event.COMPARE_PAPERS.compare( ( Event ) e1, ( Event ) e2 );
    }

    public int sortByLocation( Object e1, Object e2 ) {
        return Event.COMPARE_LOCATION.compare( ( Event ) e1, ( Event ) e2 );
    }

    public void setVariantService( VariantService variantService ) {
        this.variantService = variantService;
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }

    public void setStatsService( StatsService statsService ) {
        this.statsService = statsService;
    }

    public void paperInfo() {
        String INFOTEXT = "The information here is the source variant information parsed from the supplementary tables, documents, or directly from the main text. "
                + "<br/><br/>"
                + "These data are unfiltered and contains more information than what is displayed in the variant search results. "
                
                + "<br/><br/>"
                + "This representation also precedes any modifications such as harmonization between different notation systems, or different coordinate assemblies (e.g. variant under the Hg18 assembly are displayed as is here, but lifted-over to Hg19 in the variant search results.) "
                + "<br/><br/>"
                + "The results in the variant results table resolves such discrepancies. "
                
                + "Different papers may have different fields."
                ;
        

        FacesContext.getCurrentInstance().addMessage( null,
                new FacesMessage( FacesMessage.SEVERITY_INFO, "About:", INFOTEXT ) );
    }

}