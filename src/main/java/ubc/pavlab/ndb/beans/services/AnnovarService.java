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
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.dao.AnnovarDAO;
import ubc.pavlab.ndb.model.Annovar;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.dto.AnnovarDTO;
import ubc.pavlab.ndb.model.enums.Category;

/**
 * Service layer on top of AnnovarDAO. Contains methods for fetching information related to annovar from
 * the database and creating Annovar objects.
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean
@ApplicationScoped
public class AnnovarService implements Serializable {

    private static final long serialVersionUID = 7725957614038591228L;

    private static final Logger log = Logger.getLogger( AnnovarService.class );

    @ManagedProperty("#{daoFactoryBean}")
    private DAOFactoryBean daoFactoryBean;

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    private AnnovarDAO annovarDAO;

    /**
     * 
     */
    public AnnovarService() {
        log.info( "AnnovarService created" );
    }

    @PostConstruct
    public void init() {
        log.info( "AnnovarService init" );
        annovarDAO = daoFactoryBean.getDAOFactory().getAnnovarDAO();

    }

    protected Annovar fetchById( Integer id ) {
        if ( id == null ) {
            return null;
        }

        return map( annovarDAO.find( id ) );

    }

    protected Annovar fetchByVariantId( Integer vid ) {
        if ( vid == null ) {
            return null;
        }

        return map( annovarDAO.findByVariantId( vid ) );

    }

    protected List<Integer> fetchVariantIdsByGeneId( Integer id ) {
        if ( id == null ) {
            return null;
        }

        return annovarDAO.findVariantIdsByGeneId( id );

    }

    private Annovar map( AnnovarDTO dto ) {
        if ( dto == null ) {
            return null;
        }

        List<String> funcRefGene = StringUtils.isBlank( dto.getFuncRefGene() ) ? new ArrayList<String>()
                : Arrays.asList( dto.getFuncRefGene().split( ";" ) );
        List<String> geneRefGene = StringUtils.isBlank( dto.getGeneRefGene() ) ? new ArrayList<String>()
                : Arrays.asList( dto.getGeneRefGene().split( ";" ) );
        List<String> exonicFuncRefGene = StringUtils.isBlank( dto.getExonicFuncRefGene() ) ? new ArrayList<String>()
                : Arrays.asList( dto.getExonicFuncRefGene().split( ";" ) );
        List<String> aaChangeRefGene = StringUtils.isBlank( dto.getAaChangeRefGene() ) ? new ArrayList<String>()
                : Arrays.asList( dto.getAaChangeRefGene().split( ";" ) );

        List<Category> categories = new ArrayList<>();

        for ( String func : exonicFuncRefGene ) {
            try {
                categories.add( Category.getEnum( func ) );
            } catch ( IllegalArgumentException e ) {
                log.warn( "Unknown Category (" + func + " )" );
            }
        }

        List<Integer> geneIds = annovarDAO.findGeneIdsForAnnovarId( dto.getId() );
        List<Gene> genes = new ArrayList<>();

        for ( Integer geneId : geneIds ) {
            genes.add( cacheService.getGeneById( geneId ) );
        }

        return new Annovar( dto, funcRefGene, geneRefGene, exonicFuncRefGene, aaChangeRefGene, genes,
                categories );

    }

    public void setDaoFactoryBean( DAOFactoryBean daoFactoryBean ) {
        this.daoFactoryBean = daoFactoryBean;
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }

}
