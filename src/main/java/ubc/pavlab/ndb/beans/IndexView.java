/*
 * The ndb project
 * 
 * Copyright (c) 2015 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.services.CacheService;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Paper;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean
@ViewScoped
public class IndexView implements Serializable {

    private static final long serialVersionUID = -2394751566929159597L;
    private static final Logger log = Logger.getLogger( IndexView.class );

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    private Integer geneId;
    private String chromosome;
    private Integer start;
    private Integer stop;

    public IndexView() {
        log.info( "IndexView created" );

    }

    @PostConstruct
    public void postConstruct() {
        log.info( "IndexView postConstruct" );

    }

    public String searchByGeneSymbol() {
        return "variant?faces-redirect=true&NCBIGeneId=" + geneId;
    }

    public String searchByCoordinates() {
        return "variant?faces-redirect=true&chr=" + chromosome + "&start=" + start + "&stop=" + stop;
    }

    public List<Gene> completeSymbol( String query ) {
        log.info( query );
        return cacheService.searchGeneBySymbol( query );
    }

    public List<Paper> completeAuthor( String query ) {
        log.info( query );
        return cacheService.searchPaperByAuthor( query );
    }

    public Integer getGeneId() {
        return geneId;
    }

    public void setGeneId( Integer geneId ) {
        this.geneId = geneId;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome( String chromosome ) {
        this.chromosome = chromosome;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart( Integer start ) {
        this.start = start;
    }

    public Integer getStop() {
        return stop;
    }

    public void setStop( Integer stop ) {
        this.stop = stop;
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }

}
