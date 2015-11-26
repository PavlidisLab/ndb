package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.services.VariantService;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.model.Variant;

@ManagedBean
@ViewScoped
public class PaperView implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -401169062920170155L;

    private static final Logger log = Logger.getLogger( PaperView.class );

    @ManagedProperty("#{variantService}")
    private VariantService variantService;

    List<Variant> mutations;
    List<SimpleEntry<String, String>> instructions = new ArrayList<SimpleEntry<String, String>>();
    Paper selectedPaper;

    int numVariants;
    int numEvents;
    private List<Gene> genes;

    public List<SimpleEntry<String, String>> getInstructions() {
        return instructions;
    }

    public void setInstructions( List<SimpleEntry<String, String>> instructions ) {
        this.instructions = instructions;
    }

    public Paper getSelectedPaper() {
        return selectedPaper;
    }

    public void setSelectedPaper( Paper paper ) {
        this.selectedPaper = paper;
    }

    @PostConstruct
    public void init() {
        if ( FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest() ) {
            return; // Skip ajax requests.
        }

        log.info( "init" );
        Map<String, String> requestParams = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();

        String variantId = requestParams.get( "variantId" );

        this.selectedPaper = this.variantService.fetchById( Integer.parseInt( variantId ) ).getPaper();

        this.instructions = new ArrayList<SimpleEntry<String, String>>();
        this.instructions.add( new SimpleEntry<String, String>( "Apply liftover from hg18 ",
                "In order to use this data, we must apply liftover. This is how it's done" ) );
        this.instructions.add( new SimpleEntry<String, String>( "Correct the base reporting",
                "The base reporting is incorrect. This is how we fixed it." ) );

        List<Variant> variants = this.variantService.fetchByPaperId( this.selectedPaper.getId() );

        this.numVariants = variants.size();

        Set<Gene> genes = new HashSet<Gene>();
        List<Integer> events = new ArrayList<Integer>();
        for ( Variant v : variants ) {
            events.add( v.getEventId() );
            for ( Gene g : v.getGenes() ) {
                genes.add( g );
            }
        }

        this.genes = new ArrayList<Gene>( genes );
        Collections.sort( this.genes );

        Set<Integer> uniqueEvents = new HashSet<Integer>( events );
        this.numEvents = uniqueEvents.size();

    }

    public void setVariantService( VariantService variantService ) {
        this.variantService = variantService;
    }

    public int getNumVariants() {
        return numVariants;
    }

    public void setNumVariants( int numVariants ) {
        this.numVariants = numVariants;
    }

    public int getNumEvents() {
        return numEvents;
    }

    public void setNumEvents( int numEvents ) {
        this.numEvents = numEvents;
    }

    public List<Gene> getGenes() {
        return genes;
    }
}