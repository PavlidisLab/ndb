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

import com.mysql.fabric.xmlrpc.base.Params;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

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

    private static final int LAZY_LOAD_MAX_SIZE = 10;

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

    private MenuModel breadcrumbsModel;

    private String[] breadcrumbsLinks;
    private String[] breadcrumbsTexts;

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
        String sampleLike = requestParams.get( "sampleLike" );
        Boolean sampleExact = Boolean.parseBoolean( requestParams.get("sampleExact" ));

        String breadcrumbsLinksStr = null;
        String breadcrumbsTextsStr = null;
        String breadcrumbsCurrentStr = null;

        if ( !StringUtils.isBlank( ncbiGeneId ) ) {
            // Search by Gene
            try {
                Integer geneId = Integer.parseInt( ncbiGeneId );
                this.query = cacheService.getGeneById( geneId ).getSymbol();
                this.variants = this.variantService.fetchByGeneId( geneId );
                breadcrumbsCurrentStr = "Search by gene";

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
                    breadcrumbsLinksStr = "/publications.xhtml,/paper.xhtml?paperId=" + paperId;
                    breadcrumbsTextsStr = "Publications," + this.query + "";
                    breadcrumbsCurrentStr = "Search by paper";
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

                    breadcrumbsLinksStr = "/stats.xhtml";
                    breadcrumbsTextsStr = "Statistics";
                    breadcrumbsCurrentStr = "Search by overlap";
                }

            } catch (NullPointerException e ){
                throw new NullPointerException( "A pointer was exceptionally null." );
            }catch ( NumberFormatException e) {
                throw new IllegalArgumentException( "Malformed Search Parameters" );
            }

        } else if ( !StringUtils.isBlank( chr ) && !StringUtils.isBlank( start ) && !StringUtils.isBlank( stop ) ) {
            // Search by coordinates
            try {
                Integer startCoord = Integer.parseInt( start );
                Integer stopCoord = Integer.parseInt( stop );

                this.query = chr + ":" + start + "-" + stop;
                this.variants = this.variantService.fetchByPosition( chr, startCoord, stopCoord );

                breadcrumbsCurrentStr = "Search by coordinates";
            } catch ( NumberFormatException e ) {
                throw new IllegalArgumentException( "Invalid Coordinates: Position out of bounds of chromosome " + chr + ".");
            }
        } else if ( !StringUtils.isBlank( sampleLike ) ) {
            // Search by sample
            try {
                String searchSampleAs = sampleLike;
                if (sampleExact){
                    this.query = sampleLike;
                } else {
                    searchSampleAs = '%'+sampleLike+'%';
                    this.query = "*" + sampleLike + "*";
                }

                this.variants = this.variantService.fetchBySample( searchSampleAs );

                breadcrumbsCurrentStr = "Search by sample";
            } catch ( Exception e ) {
                throw new IllegalArgumentException( "Invalid Sample: cannot parse sample " + sampleLike + ".");
            }
        } else {
            // Unknown Search
            throw new IllegalArgumentException( "Unknown Search Parameters" );
        }
        // Generated breadcrumbs
        this.breadcrumbsLinks = parseBreadcrumbs( breadcrumbsLinksStr );
        this.breadcrumbsTexts = parseBreadcrumbs( breadcrumbsTextsStr );
        setupBreadcrumbs( breadcrumbsCurrentStr );

        this.events = Event.groupVariants( this.variants );

        this.liveScroll = ( this.events.size() > LAZY_LOAD_MAX_SIZE );

        for ( Event event : events ) {
            complexVariant |= event.isComplex();
        }

        csvExporter = new CSVExporter( "variants.tsv" );

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

    public int sortByFuncs( Object e1, Object e2 ) {
        return Event.COMPARE_FUNCS.compare( ( Event ) e1, ( Event ) e2 );
    }

    public int sortByValidation( Object e1, Object e2 ) {
        return Event.COMPARE_FUNCS.compare( ( Event ) e1, ( Event ) e2 );
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
                + "<br/><br/>" + "The results in the variant results table resolves such discrepancies. "

        + "Different papers may have different fields.";

        FacesContext.getCurrentInstance().addMessage( null,
                new FacesMessage( FacesMessage.SEVERITY_INFO, "About:", INFOTEXT ) );
    }

    public MenuModel getBreadcrumbsModel() {
        return breadcrumbsModel;
    }

    private void setupBreadcrumbs( String current ) {
        this.breadcrumbsModel = new DefaultMenuModel();

        DefaultMenuItem item = new DefaultMenuItem( "" );
        item.setUrl( "#" );
        item.setIcon( "ui-icon-home" );
        this.breadcrumbsModel.addElement( item );

        item = new DefaultMenuItem( "Home" );
        item.setUrl( "/index.xhtml" );
        this.breadcrumbsModel.addElement( item );

        for ( int i = 0; i < this.breadcrumbsLinks.length; i++ ) {
            String text = this.breadcrumbsTexts[i];
            String link = this.breadcrumbsLinks[i];

            if ( text == null ) {
                continue;
            }

            item = new DefaultMenuItem( text );
            item.setUrl( link );
            this.breadcrumbsModel.addElement( item );
        }

        if ( current == null ) {
            item = new DefaultMenuItem( "Search" );
        } else {
            item = new DefaultMenuItem( current );
        }
        item.setUrl( "#" );
        this.breadcrumbsModel.addElement( item );

    }

    private String[] parseBreadcrumbs( String bc ) {
        // Use fro comma-delimited breadcrumbs
        if ( bc == null ) {
            String[] nullArray = new String[1];
            nullArray[0] = null;
            return nullArray;
        }
        return bc.split( "," );
    }

    public int tableHeight(){
	//Stub. Update as needed.
        if (variants == null) {
            return 25;
        }

        return Math.min( 25*variants.size() + 25, 500);

    }

    // public String getBreadcrumbsLinks() {
    // public String[] getBreadcrumbsLinks() {
    // // Link to previous page
    // if ( breadcrumbsLinks == null || breadcrumbsLinks.isEmpty() ) {
    // return this.parseBreadcrumbs( "" );
    // }
    // return this.parseBreadcrumbs( breadcrumbsLinks );
    // // return breadcrumbsLinks;
    // }
    //
    // // public String getBreadcrumbsTexts() {
    // public String[] getBreadcrumbsTexts() {
    // // Text for link to previous page
    // if ( breadcrumbsTexts == null || breadcrumbsTexts.isEmpty() ) {
    // return this.parseBreadcrumbs( "" );
    // }
    // return this.parseBreadcrumbs( breadcrumbsTexts );
    // // return breadcrumbsTexts;
    // }

}