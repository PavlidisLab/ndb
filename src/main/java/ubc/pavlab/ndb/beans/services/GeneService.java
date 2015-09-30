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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.dao.AnnovarDAO;
import ubc.pavlab.ndb.dao.GeneDAO;
import ubc.pavlab.ndb.dao.LOFBreakdownDAO;
import ubc.pavlab.ndb.exceptions.DAOException;
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

    private GeneDAO geneDAO;
    private AnnovarDAO annovarDAO;
    private LOFBreakdownDAO lofBreakdownDAO;

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

    }

    /**
     * Returns the gene from the database matching the given ID, otherwise null.
     * 
     * @param id The ID of the gene to be returned.
     * @return The gene from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public Gene fetchGene( Integer id ) {
        return map( geneDAO.find( id ) );
    }

    /**
     * Returns the gene from the database matching the given symbol, otherwise null.
     * 
     * @param symbol The symbol of the gene to be returned.
     * @return The gene from the database matching the given symbol, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public Gene fetchGene( String symbol ) {
        return map( geneDAO.find( symbol ) );
    }

    /**
     * Returns a list of all genes from the database ordered by gene ID. The list is never null and
     * is empty when the database does not contain any genes.
     * 
     * @return A list of all genes from the database ordered by gene ID.
     * @throws DAOException If something fails at database level.
     */
    public List<Gene> listGenes() {
        // Done in this manner to minimize DB hits
        Map<Integer, GeneBuilder> builderMap = new LinkedHashMap<>();

        List<GeneDTO> geneDTOs = geneDAO.list();

        for ( GeneDTO dto : geneDTOs ) {
            builderMap.put( dto.getId(), new GeneBuilder( dto.getId(), dto.getSymbol(), dto.getSize() ) );
        }

        geneDTOs.clear();

        List<AnnovarDTO> annovarDTOs = annovarDAO.list();

        for ( AnnovarDTO dto : annovarDTOs ) {
            GeneBuilder builder = builderMap.get( dto.getGeneId() );
            if ( builder != null ) {
                builder.annovarSymbol( dto.getSymbol() );
            } else {
                log.warn( String.format( "Annovar Symbol matched to non-existent Gene ID: %s", dto.getGeneId() ) );
            }
        }

        annovarDTOs.clear();

        List<LOFBreakdownDTO> lofDTOs = lofBreakdownDAO.list();

        for ( LOFBreakdownDTO dto : lofDTOs ) {
            GeneBuilder builder = builderMap.get( dto.getGene_id() );
            if ( builder != null ) {
                builder.lofBreakdown( dto );
            } else {
                log.warn( String.format( "LOF Breakdown matched to non-existent Gene ID: %s", dto.getGene_id() ) );
            }
        }

        List<Gene> geneList = new ArrayList<>();

        for ( GeneBuilder builder : builderMap.values() ) {
            geneList.add( builder.build() );
        }

        return geneList;
    }

    private Gene map( GeneDTO geneDTO ) {
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
}
