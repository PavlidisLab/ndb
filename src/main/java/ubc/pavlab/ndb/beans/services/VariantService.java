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

import com.google.common.collect.Lists;

import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.dao.VariantDAO;
import ubc.pavlab.ndb.model.Annovar;
import ubc.pavlab.ndb.model.CglibProxyFactory;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.LazilyLoadedObject;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.model.RawKV;
import ubc.pavlab.ndb.model.Variant;
import ubc.pavlab.ndb.model.dto.VariantDTO;
import ubc.pavlab.ndb.model.enums.Category;

/**
 * Service layer on top of VariantDAO. Contains methods for fetching information related to variants from
 * the database and creating Variant objects.
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean
@ApplicationScoped
public class VariantService implements Serializable {

    private static final long serialVersionUID = 7725957614038591228L;

    private static final Logger log = Logger.getLogger( VariantService.class );

    private static final boolean LAZY_LOAD_ANNOVAR = true;
    private static final boolean LAZY_LOAD_RAWKV = true;

    @ManagedProperty("#{daoFactoryBean}")
    private DAOFactoryBean daoFactoryBean;

    @ManagedProperty("#{annovarService}")
    private AnnovarService annovarService;

    @ManagedProperty("#{rawKVService}")
    private RawKVService rawKVService;

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    private VariantDAO variantDAO;

    private CglibProxyFactory proxyFactory = new CglibProxyFactory();

    /**
     * 
     */
    public VariantService() {
        log.info( "VariantService created" );
    }

    @PostConstruct
    public void init() {
        log.info( "VariantService init" );
        variantDAO = daoFactoryBean.getDAOFactory().getVariantDAO();

    }

    public Variant fetchById( Integer id ) {
        if ( id == null ) {
            return null;
        }
        return map( variantDAO.find( id ) );

    }

    public List<Variant> fetchById( List<Integer> ids ) {
        if ( ids == null || ids.isEmpty() ) {
            return Lists.newArrayList();
        }
        return map( variantDAO.find( ids ) );

    }

    public List<Variant> fetchByPaperId( Integer id ) {
        if ( id == null ) {
            return Lists.newArrayList();
        }
        return map( variantDAO.findByPaperId( id ) );

    }

    public List<Variant> fetchByEventId( Integer id ) {
        if ( id == null ) {
            return Lists.newArrayList();
        }
        return map( variantDAO.findByEventId( id ) );

    }

    public List<Variant> fetchBySubjectId( Integer id ) {
        if ( id == null ) {
            return Lists.newArrayList();
        }
        return map( variantDAO.findBySubjectId( id ) );

    }

    public List<Variant> fetchByPosition( String chr, Integer start, Integer stop ) {
        if ( chr == null || start == null || stop == null ) {
            return Lists.newArrayList();
        }
        return map( variantDAO.findByPosition( chr, start, stop ) );
    }

    public List<Variant> fetchByGeneId( Integer geneId ) {
        if ( geneId == null ) {
            return Lists.newArrayList();
        }
        //TODO Inefficient; too many lookups... find a clean way of implementing normalized lookups for this kind of use case
        List<Integer> variantIds = variantDAO.findVariantIdsForGeneId( geneId );
        return fetchById( variantIds );

    }

    @SuppressWarnings("unchecked")
    private Variant map( VariantDTO dto ) {
        if ( dto == null ) {
            return null;
        }

        Category category = null;
        try {
            category = Category.getEnum( dto.getCategory() );
        } catch ( IllegalArgumentException e ) {
            log.warn( "Unknown Category (" + dto.getCategory() + " )" );
        }
        // Populate Genes

        List<Integer> geneIds = variantDAO.findGeneIdsForVariantId( dto.getId() );
        List<Gene> genes = new ArrayList<>();

        for ( Integer geneId : geneIds ) {
            genes.add( cacheService.getGeneById( geneId ) );
        }

        // Get Annovar Information
        Annovar annovar = null;
        if ( LAZY_LOAD_ANNOVAR ) {
            // This version requires a no argument constructor for the proxied object
            //            annovar = ( Annovar ) Enhancer.create(
            //                    Annovar.class,
            //                    new LazyLoader() {
            //
            //                        @Override
            //                        public Object loadObject() throws Exception {
            //                            return annovarService.fetchByVariantId( vid );
            //                        }
            //                    } );

            annovar = proxyFactory.createProxy( Annovar.class, new LazilyLoadedObject( dto.getId()) {
                @Override
                protected Object loadObject() {
                    return annovarService.fetchByVariantId( ( Integer ) this.ids[0] );
                }
            } );

        } else {
            annovar = annovarService.fetchByVariantId( dto.getId() );
        }

        // Get RawKV Information
        List<RawKV> rawKV = null;
        if ( LAZY_LOAD_RAWKV ) {

            rawKV = proxyFactory.createProxy( List.class,
                    new LazilyLoadedObject( dto.getPaperId(), dto.getRawVariantId()) {
                        @Override
                        protected Object loadObject() {
                            return rawKVService.fetchByPaperAndRawVariantId( ( Integer ) this.ids[0],
                                    ( Integer ) this.ids[1] );
                        }
                    } );

        } else {
            rawKV = rawKVService.fetchByPaperAndRawVariantId( dto.getPaperId(), dto.getRawVariantId() );
        }

        // Get paper Information from cache
        Paper paper = cacheService.getPaperById( dto.getPaperId() );

        return new Variant( dto, annovar, rawKV, paper, genes, category );
    }

    private List<Variant> map( List<VariantDTO> dtos ) {
        if ( dtos == null || dtos.isEmpty() ) {
            return Lists.newArrayList();
        }
        List<Variant> variants = Lists.newArrayList();
        for ( VariantDTO dto : dtos ) {
            variants.add( map( dto ) );
        }
        return variants;
    }

    public void setDaoFactoryBean( DAOFactoryBean daoFactoryBean ) {
        this.daoFactoryBean = daoFactoryBean;
    }

    public void setAnnovarService( AnnovarService annovarService ) {
        this.annovarService = annovarService;
    }

    public void setRawKVService( RawKVService rawKVService ) {
        this.rawKVService = rawKVService;
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }

}
