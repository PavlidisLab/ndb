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

import ubc.pavlab.ndb.beans.services.CacheService;
import ubc.pavlab.ndb.beans.services.StatsService;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.utility.Tuples.Tuple2;

@ManagedBean
@ViewScoped
public class PaperView implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -401169062920170155L;

    private static final Logger log = Logger.getLogger( PaperView.class );

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    @ManagedProperty("#{statsService}")
    private StatsService statsService;

    private Paper paper;
    private int variantCnt;
    private int eventCnt;
    private List<Tuple2<String, Integer>> eventCntByContext;
    private List<Tuple2<String, Integer>> eventCntByCategory;

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

        //variantCnt = statsService.getVariantCntByPaperId( paperId );
        eventCnt = statsService.getEventCntByPaperId( paperId );
        //        eventCntByContext = statsService.getEventCntByContext( paperId );
        eventCntByCategory = statsService.getEventCntByCategory( paperId );

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

    public List<Tuple2<String, Integer>> getEventCntByContext() {
        return eventCntByContext;
    }

    public List<Tuple2<String, Integer>> getEventCntByCategory() {
        return eventCntByCategory;
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }

    public void setStatsService( StatsService statsService ) {
        this.statsService = statsService;
    }

}