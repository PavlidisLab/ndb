package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.services.VariantService;
import ubc.pavlab.ndb.model.Event;
import ubc.pavlab.ndb.model.EventListFactory;
import ubc.pavlab.ndb.model.Variant;

@ManagedBean
@ViewScoped
public class ResultsView implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -506601459390058684L;

    private static final Logger log = Logger.getLogger( ResultsView.class );

    private String query;

    @ManagedProperty("#{variantService}")
    private VariantService variantService;

    List<Variant> mutations;
    List<Event> events;

    Variant selectedVariant;

    public Variant getSelectedVariant() {
        return selectedVariant;
    }

    public void setSelectedVariant( Variant trunk ) {
        this.selectedVariant = trunk;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents( List<Event> events ) {
        this.events = events;
    }

    public List<Variant> getMutations() {
        return mutations;
    }

    public void setMutations( List<Variant> mutations ) {
        this.mutations = mutations;
    }

    @PostConstruct
    public void init() {
        if ( FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest() ) {
            return; // Skip ajax requests.
        }
        log.info( "init" );
        Map<String, String> requestParams = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();

        this.query = requestParams.get( "query" );
        if ( this.query == null ) {
            // TODO: This is here because otherwise the row expansion view crashes
        }
        mutations = this.variantService.fetchByGeneId( Integer.parseInt( this.query ) );

        EventListFactory eventListFactory = new EventListFactory( mutations );
        events = eventListFactory.getEventList();

    }

    public String getQuery() {
        return query;
    }

    public void setQuery( String query ) {
        this.query = query;
    }

    public void setVariantService( VariantService variantService ) {
        this.variantService = variantService;
    }

}