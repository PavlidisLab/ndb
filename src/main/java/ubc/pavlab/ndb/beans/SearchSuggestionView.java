package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

/**
 * Bean for variant search suggestions based on Genes (TODO: And other things such as paper author??)
 * 
 * @author mbelmadani
 * @version $Id$
 */
@ManagedBean(name = "searchSuggestionView")
@RequestScoped
public class SearchSuggestionView implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger( SearchSuggestionView.class );

    private List<String> results = null;

    private List<String> genes = null;
    private List<String> authors = null;
    // private List<String> anotherTypeOfResult = null;

    private static SearchSuggestionView searchSuggestionView;

    public SearchSuggestionView() {
        log.info( "SearchSuggestionView created" );
    }

    // public List<String> completeArea( String query ) {
    // List<String> results = new ArrayList<String>();
    //
    // // TODO: Query Genes
    //
    // // TODO: Query Authors
    //
    // // TODO: Assemble suggestions into /results/
    //
    // if ( query.startsWith( "PrimeFaces" ) ) {
    // results.add( "PrimeFaces Crocs!!!" );
    // results.add( "PrimeFaces has 100+ components." );
    // results.add( "PrimeFaces is lightweight." );
    // results.add( "PrimeFaces is easy to use." );
    // results.add( "PrimeFaces is developed with passion!" );
    // } else {
    // for ( int i = 0; i < 10; i++ ) {
    // results.add( query + i );
    // }
    // }
    //
    // return results;
    // }

    public void buttonAction() { // ActionEvent actionEvent ) {
        addMessage( "Searching database for mutations." );
    }

    public void addMessage( String summary ) {
        FacesMessage message = new FacesMessage( FacesMessage.SEVERITY_INFO, summary, null );
        FacesContext.getCurrentInstance().addMessage( null, message );
    }

    public static SearchSuggestionView getSearchSuggestionView() {
        return searchSuggestionView;
    }

    public static void setSearchSuggestionView( SearchSuggestionView searchSuggestionView ) {
        SearchSuggestionView.searchSuggestionView = searchSuggestionView;
    }
}