package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.services.CacheService;
import ubc.pavlab.ndb.beans.services.StatsService;
import ubc.pavlab.ndb.model.Paper;

@ManagedBean
@ViewScoped
public class PublicationsView implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -401169062920170155L;
    private static final Logger log = Logger.getLogger( PublicationsView.class );

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    @ManagedProperty("#{statsService}")
    private StatsService statsService;

    private List<Paper> papers;
    private List<Paper> filteredPapers;


    public PublicationsView() {
        log.info( "create PublicationsView" );
    }

    @PostConstruct
    public void init() {
        if ( FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest() ) {
            return; // Skip ajax requests.
        }
        log.info( "init PublicationsView" );
        setPapers( new ArrayList<>( cacheService.listPapers() ) );
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }
    public void setStatsService( StatsService statsService ) {
        this.statsService = statsService;
    }

    /**
     * @return the papers
     */
    public List<Paper> getPapers() {
        return papers;
    }

    /**
     * @param papers the papers to set
     */
    public void setPapers( List<Paper> papers ) {
        this.papers = papers;
    }

    public List<Paper> getFilteredPapers() {
        return filteredPapers;
    }

    public void setFilteredPapers(List<Paper> filteredPapers) {
        this.filteredPapers = filteredPapers;
    }

    public Integer getEventCnt(Integer paperId) {

        Integer eventCnt = statsService.getEventCntByPaperId( paperId );
        return eventCnt;
    }

}