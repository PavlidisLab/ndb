package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

//TODO: add DAO and 
import ubc.pavlab.ndb.model.Mutation;
import ubc.pavlab.ndb.service.MutationService;

@ManagedBean(name = "variantView")
public class VariantView implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4032558721810259504L;

    private List<Mutation> mutations;
    private Mutation trunk;

    @ManagedProperty("#{mutationService}")
    private MutationService service;

    @PostConstruct
    public void init() {
        service = new MutationService();

        String variantId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( "id" );
        trunk = service.getTrunkMutation( Integer.parseInt( variantId ) );
        mutations = service.getRawMutations( Integer.parseInt( variantId ) );
    }

    public int getOccurences() {
        return mutations.size();
    }

    public Mutation getTrunk() {
        return trunk;
    }

    public void setTrunk( Mutation trunk ) {
        this.trunk = trunk;
    }

    public List<Mutation> getMutations() {
        return mutations;
    }

    public void setService( MutationService service ) {
        this.service = service;
    }
}