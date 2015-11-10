package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import ubc.pavlab.ndb.beans.services.VariantService;
import ubc.pavlab.ndb.model.Variant;

@ManagedBean(name = "resultsView")
public class ResultsView implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -506601459390058684L;

    private List<Variant> mutations;

    private String query;

    @ManagedProperty("#{variantService}")
    private VariantService variantService;

    @PostConstruct
    public void init() {
        Map<String, String> requestParams = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();

        this.query = requestParams.get( "query" );
        mutations = this.variantService.fetchByGeneId( Integer.parseInt( this.query ) );

    }

    public List<Variant> getMutations() {
        return mutations;
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