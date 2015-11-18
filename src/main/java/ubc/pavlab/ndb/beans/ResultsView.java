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
            this.query = "2084";
        }
        mutations = this.variantService.fetchByGeneId( Integer.parseInt( this.query ) );
        // this.sortById();

        EventListFactory eventListFactory = new EventListFactory( mutations );
        events = eventListFactory.getEventList();

    }

    // public String sortById() {
    // /*
    // * Sorting mutations according to eventId
    // */
    // boolean sortAscending = true;
    // if ( sortAscending ) {
    // // ascending order
    // Collections.sort( this.mutations, new Comparator<Variant>() {
    // @Override
    // public int compare( Variant v1, Variant v2 ) {
    // return v1.getEventId().compareTo( v2.getEventId() );
    // }
    // } );
    // sortAscending = false;
    // } else { // TODO: Implement sortAscending as a class variable to maintain toggling
    // // descending order
    // Collections.sort( this.mutations, new Comparator<Variant>() {
    // @Override
    // public int compare( Variant v1, Variant v2 ) {
    // return v2.getEventId().compareTo( v1.getEventId() );
    // }
    // } );
    // sortAscending = true;
    // }
    // return null;
    // }

    // public List<Variant> getMutations() {
    // return mutations;
    // }
    //
    // public List<Variant> getUniqueMutations() {
    // return uniqueMutations;
    // }

    public String getQuery() {
        return query;
    }

    public String printNumber( int i ) {
        int ip1 = i + 1;
        return Integer.toString( ip1 );
    }

    public void setQuery( String query ) {
        this.query = query;
    }

    public void setVariantService( VariantService variantService ) {
        this.variantService = variantService;
    }

}