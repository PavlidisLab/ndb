package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
public class AllPapersView implements Serializable {

    /**
     * View to display all the papers.
     * 
     * @author mbelmadani
     * @version $Id$
     */
    private static final long serialVersionUID = 5738460619335677599L;

    private static final Logger log = Logger.getLogger( AllPapersView.class );

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    @ManagedProperty("#{statsService}")
    private StatsService statsService;

    private Paper paper;
    private List<Paper> papers;

    private int variantCnt;
    private int eventCnt;
    private List<Tuple2<String, Integer>> eventCntByContext;
    private List<Tuple2<String, Integer>> eventCntByCategory;

    public AllPapersView() {
        log.info( "create AllPapersView" );
    }

    @PostConstruct
    public void init() {
        if ( FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest() ) {
            return; // Skip ajax requests.
        }

        log.info( "init AllPapersView" );
        Map<String, String> requestParams = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();

        int paperId = 1;
        paper = cacheService.getPaperById( paperId );

        papers = new ArrayList<Paper>( cacheService.listPapers() );

        // variantCnt = statsService.getVariantCntByPaperId( paperId );
        eventCnt = statsService.getEventCntByPaperId( paperId );
        // eventCntByContext = statsService.getEventCntByContext( paperId );
        eventCntByCategory = statsService.getEventCntByCategory( paperId );

    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }

    public void setStatsService( StatsService statsService ) {
        this.statsService = statsService;
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

    public Paper getPaper() {
        return paper;
    }

    public void setPaper( Paper paper ) {
        this.paper = paper;
    }

    public List<Paper> getPapers() {
        return this.papers;
    }

    public void setPapers( Collection<Paper> papers ) {
        this.papers = new ArrayList<Paper>( papers );
    }

}