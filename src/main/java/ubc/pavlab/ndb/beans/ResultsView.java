package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

//TODO: add DAO and 
import ubc.pavlab.ndb.model.Mutation;
import ubc.pavlab.ndb.service.MutationService;

@ManagedBean(name = "resultsView")
public class ResultsView implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -506601459390058684L;

    private List<Mutation> mutations;
    private String query;

    @ManagedProperty("#{mutationService}")
    private MutationService service;

    @PostConstruct
    public void init() {
        this.setQuery( "SEARCH QUERY" );
        service = new MutationService();
        mutations = service.createMutations();
    }

    public List<Mutation> getMutations() {
        return mutations;
    }

    public void setService( MutationService service ) {
        this.service = service;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery( String query ) {
        this.query = query;
    }
}