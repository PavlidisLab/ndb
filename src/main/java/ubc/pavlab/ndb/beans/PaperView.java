package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.services.CacheService;
import ubc.pavlab.ndb.beans.services.VariantService;
import ubc.pavlab.ndb.model.Paper;

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

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    private Paper paper;
    private int variantCnt;
    private int eventCnt;

    public PaperView() {
        log.info( "create PaperView" );
    }

    @PostConstruct
    public void init() {
        if ( FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest() ) {
            return; // Skip ajax requests.
        }

        log.info( "init PaperView" );
        Map<String, String> requestParams = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();

        int paperId;
        try {
            paperId = Integer.valueOf( requestParams.get( "paperId" ) );
        } catch ( NumberFormatException | NullPointerException e ) {
            throw new IllegalArgumentException( "Malformed Search Parameters" );
        }

        paper = cacheService.getPaperById( paperId );

        variantCnt = cacheService.getVariantCntByPaperId( paperId );
        eventCnt = cacheService.getEventCntByPaperId( paperId );

    }

    public Paper getPaper() {
        return paper;
    }

    public int getVariantCnt() {
        return variantCnt;
    }

    public int getEventCnt() {
        return eventCnt;
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }

    public void setVariantService( VariantService variantService ) {
        this.variantService = variantService;
    }

}