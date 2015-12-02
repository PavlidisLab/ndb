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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.services.CacheService;
import ubc.pavlab.ndb.model.Gene;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean
@ViewScoped
public class SearchView implements Serializable {

    private static final long serialVersionUID = -1031555262870044904L;
    private static final Logger log = Logger.getLogger( SearchView.class );

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    private Integer geneId;
    private String region;

    public SearchView() {
        log.info( "SearchView created" );

    }

    @PostConstruct
    public void postConstruct() {
        log.info( "SearchView postConstruct" );

    }

    public String searchByGeneSymbol() {
        return geneId == 0 || geneId == null ? null : "variant?faces-redirect=true&NCBIGeneId=" + geneId;
    }

    public String searchByRegion() {

        if ( region.matches( "^(CHR)?([0-9]{1,2}|X|Y):(\\d+)-(\\d+|)$" ) ) {
            String chromosome;
            String start;
            String stop;
            try {
                String[] split = StringUtils.removeStart( region.toLowerCase(), "chr" ).split( "[\\:\\-]" );
                chromosome = split[0];
                start = split[1];
                stop = split[2];

            } catch ( IndexOutOfBoundsException e ) {
                return null;
            }
            return "variant?faces-redirect=true&chr=" + chromosome + "&start=" + start + "&stop=" + stop;
        } else {
            return null;
        }

    }

    public List<Gene> completeSymbol( String query ) {
        log.info( query );
        return cacheService.searchGeneBySymbol( query );
    }

    public Integer getGeneId() {
        return geneId;
    }

    public void setGeneId( Integer geneId ) {
        this.geneId = geneId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion( String region ) {
        this.region = region;
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }

}
