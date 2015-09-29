package ubc.pavlab.ndb.beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "filterView")
public class FilterView {

    private String[] selectedVariants;
    private List<String> variants;

    @PostConstruct
    public void init() {
        variants = new ArrayList<String>();
        variants.add( "Missense" );
        variants.add( "Non-sense" );
        variants.add( "5' UTR" );
        variants.add( "3' UTR" );
        variants.add( "No impact" );
    }

    public String[] getSelectedVariants() {
        return selectedVariants;
    }

    public void setSelectedVariants( String[] selectedVariants ) {
        this.selectedVariants = selectedVariants;
    }

    public List<String> getVariants() {
        return variants;
    }
}