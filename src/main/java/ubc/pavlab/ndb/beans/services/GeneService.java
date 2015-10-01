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

package ubc.pavlab.ndb.beans.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.dao.AnnovarDAO;
import ubc.pavlab.ndb.dao.GeneDAO;
import ubc.pavlab.ndb.dao.LOFBreakdownDAO;
import ubc.pavlab.ndb.exceptions.DAOException;
import ubc.pavlab.ndb.exceptions.GeneNotFoundException;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Gene.GeneBuilder;
import ubc.pavlab.ndb.model.dto.AnnovarDTO;
import ubc.pavlab.ndb.model.dto.GeneDTO;
import ubc.pavlab.ndb.model.dto.LOFBreakdownDTO;

/**
 * Service layer on top of GeneDAO. Contains methods for fetching information related to genes from the database
 * and creating Gene objects.
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean
@ApplicationScoped
public class GeneService implements Serializable {

    private static final long serialVersionUID = 7725957614038591228L;

    private static final Logger log = Logger.getLogger( GeneService.class );

    @ManagedProperty("#{daoFactoryBean}")
    private DAOFactoryBean daoFactoryBean;

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    private GeneDAO geneDAO;
    private AnnovarDAO annovarDAO;
    private LOFBreakdownDAO lofBreakdownDAO;

    private LoadingCache<Integer, Gene> cache;

    /**
     * 
     */
    public GeneService() {
        log.info( "GeneService created" );
    }

    @PostConstruct
    public void init() {
        log.info( "GeneService init" );
        geneDAO = daoFactoryBean.getDAOFactory().getGeneDAO();
        annovarDAO = daoFactoryBean.getDAOFactory().getAnnovarDAO();
        lofBreakdownDAO = daoFactoryBean.getDAOFactory().getLOFBreakdownDAO();
        cache = CacheBuilder.newBuilder()
                .build(
                        new CacheLoader<Integer, Gene>() {
                            @Override
                            public Gene load( Integer id ) throws Exception {
                                Gene g = loadFromDatabase( id );
                                if ( g != null ) {
                                    return g;
                                } else {
                                    throw new GeneNotFoundException( "Gene not found with ID: " + id );
                                }
                            }
                        } );
    }

    /**
     * Returns the gene from the database matching the given ID, otherwise null.
     * 
     * @param id The ID of the gene to be returned.
     * @return The gene from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public Gene fetchGene( Integer id ) {
        try {
            return cache.get( id );
        } catch ( ExecutionException e ) {
            log.warn( "Gene not found with ID: " + id );
            return null;
        }
    }

    /**
     * Returns the gene from the database matching the given symbol, otherwise null.
     * 
     * @param symbol The symbol of the gene to be returned.
     * @return The gene from the database matching the given symbol, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public Gene fetchGene( String symbol ) {
        Integer id = cacheService.getGeneIdForExactSymbol( symbol );
        if ( id != null ) {
            try {
                return cache.get( id );
            } catch ( ExecutionException e ) {
                log.warn( "Gene not found with ID: " + id );
                return null;
            }
        } else {
            log.warn( "Gene not found with Symbol: " + symbol );
            return null;
        }

    }

    /**
     * TODO: NOT EVEN CLOSE TO EFFICIENT
     * 
     * Returns a list of all genes from the database ordered by gene ID. The list is never null and
     * is empty when the database does not contain any genes.
     * 
     * @return A list of all genes from the database ordered by gene ID.
     * @throws DAOException If something fails at database level.
     */
    public List<Gene> listGenes() {
        List<Gene> geneList = new ArrayList<>();
        for ( GeneDTO dto : geneDAO.list() ) {
            try {
                geneList.add( cache.get( dto.getId() ) );
            } catch ( ExecutionException e ) {
                log.warn( "Gene not found with ID: " + dto.getId() );
            }

        }
        return geneList;
    }

    private Gene loadFromDatabase( Integer id ) {
        log.info( "Loading from database: " + id );
        if ( id == null ) {
            return null;
        }

        GeneDTO geneDTO = geneDAO.find( id );

        if ( geneDTO == null ) {
            return null;
        }

        List<AnnovarDTO> annovarDTOs = annovarDAO.findByGeneId( geneDTO.getId() );

        GeneBuilder builder = new GeneBuilder( geneDTO.getId(), geneDTO.getSymbol(), geneDTO.getSize() );
        for ( AnnovarDTO annovarDTO : annovarDTOs ) {
            builder.annovarSymbol( annovarDTO.getSymbol() );
        }

        LOFBreakdownDTO lof = lofBreakdownDAO.findByGeneId( geneDTO.getId() );

        builder.lofBreakdown( lof );

        return builder.build();
    }

    public void setDaoFactoryBean( DAOFactoryBean daoFactoryBean ) {
        this.daoFactoryBean = daoFactoryBean;
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }
}
