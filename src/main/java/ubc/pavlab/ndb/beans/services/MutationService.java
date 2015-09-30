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

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.dao.MutationDAO;
import ubc.pavlab.ndb.exceptions.DAOException;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Mutation;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.model.dto.MutationDTO;

/**
 * Service layer on top of MutationDAO. Contains methods for fetching information related to mutations from the database
 * and creating Mutation objects.
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean
@ApplicationScoped
public class MutationService implements Serializable {

    private static final long serialVersionUID = 5511962572600176620L;

    private static final Logger log = Logger.getLogger( MutationService.class );

    @ManagedProperty("#{daoFactoryBean}")
    private DAOFactoryBean daoFactoryBean;

    @ManagedProperty("#{paperService}")
    private PaperService paperService;

    @ManagedProperty("#{geneService}")
    private GeneService geneService;

    private MutationDAO mutationDAO;

    /**
     * 
     */
    public MutationService() {
        log.info( "MutationService created" );
    }

    @PostConstruct
    public void init() {
        log.info( "MutationService init" );
        mutationDAO = daoFactoryBean.getDAOFactory().getMutationDAO();

    }

    /**
     * Returns the mutation from the database matching the given ID, otherwise null.
     * 
     * @param id The ID of the mutation to be returned.
     * @return The mutation from the database matching the given ID, otherwise null.
     */
    public Mutation fetchMutation( Integer id ) {
        return map( mutationDAO.find( id ) );
    }

    /**
     * Returns a list of mutations from the database matching the given gene ID.
     * 
     * @param geneId The gene ID of the mutations to be returned.
     * @return A list of mutations from the database matching the given gene ID.
     */
    public List<Mutation> fetchByGeneId( Integer geneId ) {
        List<Mutation> mutationList = new ArrayList<>();
        for ( MutationDTO dto : mutationDAO.findByGeneId( geneId ) ) {
            mutationList.add( map( dto ) );
        }
        return mutationList;
    }

    /**
     * Returns a list of mutations from the database matching the given paper ID.
     * 
     * @param geneId The paper ID of the mutations to be returned.
     * @return A list of mutations from the database matching the given paper ID.
     */
    public List<Mutation> fetchByPaperId( Integer paperId ) {
        List<Mutation> mutationList = new ArrayList<>();
        for ( MutationDTO dto : mutationDAO.findByPaperId( paperId ) ) {
            mutationList.add( map( dto ) );
        }
        return mutationList;
    }

    /**
     * Returns a list of all mutations from the database ordered by ID. The list is never null and
     * is empty when the database does not contain any mutations.
     * 
     * @return A list of all mutations from the database ordered by ID.
     * @throws DAOException If something fails at database level.
     */
    public List<Mutation> listMutations() {
        List<Mutation> mutationList = new ArrayList<>();
        for ( MutationDTO dto : mutationDAO.list() ) {
            mutationList.add( map( dto ) );
        }
        return mutationList;
    }

    private Mutation map( MutationDTO dto ) {
        if ( dto == null ) {
            return null;
        }
        Paper paper = paperService.fetchPaper( dto.getPaper_id() );

        if ( paper == null ) {
            log.warn( String.format( "Could not find Paper matching id: %s", dto.getPaper_id() ) );
        }

        Gene gene = geneService.fetchGene( dto.getGene_id() );

        if ( gene == null ) {
            log.warn( String.format( "Could not find Gene matching id: %s", dto.getGene_id() ) );
        }

        return new Mutation( dto.getId(), paper, gene, dto.getSample_identifier(), dto.getChromosome(),
                dto.getHg19_start(), dto.getHg19_stop(), dto.getRef(), dto.getAlt(),
                dto.getMutation_effect(), dto.getCode_change(), dto.getProt_change(), dto.getQvalue(), dto.getSift(),
                dto.getPolyphen(), dto.getGoodmut() );
    }

    public void setDaoFactoryBean( DAOFactoryBean daoFactoryBean ) {
        this.daoFactoryBean = daoFactoryBean;
    }

    public void setPaperService( PaperService paperService ) {
        this.paperService = paperService;
    }

    public void setGeneService( GeneService geneService ) {
        this.geneService = geneService;
    }
}
