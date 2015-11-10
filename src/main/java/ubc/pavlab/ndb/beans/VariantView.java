package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import ubc.pavlab.ndb.beans.services.VariantService;
import ubc.pavlab.ndb.model.SourceMutation;
import ubc.pavlab.ndb.model.Variant;

@ManagedBean(name = "variantView")
public class VariantView implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4032558721810259504L;

    private List<Variant> mutations;
    private Variant trunk;
    private SourceMutation sourceMutation;

    @ManagedProperty("#{variantService}")
    private VariantService service;

    @PostConstruct
    public void init() {
        String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( "id" );

        mutations = service.fetchByEventId( Integer.parseInt( id ) );
        // TODO: Make sure this makes sense
        if ( !mutations.isEmpty() ) {
            trunk = ( Variant ) mutations.toArray()[0];
        }
    }

    public int getOccurences() {
        return mutations.size();
    }

    public Variant getTrunk() {
        return trunk;
    }

    public void setTrunk( Variant trunk ) {
        this.trunk = trunk;
    }

    public List<Variant> getMutations() {
        return mutations;
    }

    public SourceMutation getSourceMutation() {
        return sourceMutation;
    }

    public void setSourceMutation( SourceMutation source ) {
        this.sourceMutation = source;
    }

    public void setService( VariantService service ) {
        this.service = service;
    }
}