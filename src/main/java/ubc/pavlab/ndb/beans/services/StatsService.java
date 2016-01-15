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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.dao.StatsDAO;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.utility.Tuples.Tuple2;

/**
 * Service layer meant to be an entry point to retrieve all aggregate database statistics. Will implement caches.
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean
@ApplicationScoped
public class StatsService implements Serializable {

    private static final long serialVersionUID = -2618479583725470865L;

    private static final Logger log = Logger.getLogger( StatsService.class );

    private static final int EXPIRATION_TIME = 1;

    private static final TimeUnit EXPIRATION_TIME_UNIT = TimeUnit.HOURS;

    private static final Integer TOP_N = 10;

    @ManagedProperty("#{daoFactoryBean}")
    private DAOFactoryBean daoFactoryBean;

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    private StatsDAO statsDAO;

    private final Supplier<Integer> latestPaperCnt = Suppliers.memoizeWithExpiration( paperCntSupplier(),
            EXPIRATION_TIME, EXPIRATION_TIME_UNIT );
    private final Supplier<Integer> latestVariantCnt = Suppliers.memoizeWithExpiration( variantCntSupplier(),
            EXPIRATION_TIME, EXPIRATION_TIME_UNIT );
    private final Supplier<Integer> latestEventCnt = Suppliers.memoizeWithExpiration( eventCntSupplier(),
            EXPIRATION_TIME, EXPIRATION_TIME_UNIT );
    private final Supplier<Integer> latestSubjectCnt = Suppliers.memoizeWithExpiration( subjectCntSupplier(),
            EXPIRATION_TIME, EXPIRATION_TIME_UNIT );

    private final Supplier<List<Gene>> latestTopGenesByVariantCnt = Suppliers
            .memoizeWithExpiration( topGenesByVariantCntSupplier(), EXPIRATION_TIME, EXPIRATION_TIME_UNIT );

    private final Supplier<List<Gene>> latestTopGenesByEventCnt = Suppliers
            .memoizeWithExpiration( topGenesByEventCntSupplier(), EXPIRATION_TIME, EXPIRATION_TIME_UNIT );

    private final Supplier<List<Gene>> latestTopGenesByPaperCnt = Suppliers
            .memoizeWithExpiration( topGenesByPaperCntSupplier(), EXPIRATION_TIME, EXPIRATION_TIME_UNIT );

    // Specific Paper statistics

    private final Map<Integer, Integer> paperVariantCntCache = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> paperEventCntCache = new ConcurrentHashMap<>();

    private final Map<Integer, List<Tuple2<String, Integer>>> paperEventCntByContext = new ConcurrentHashMap<>();
    private final Map<Integer, List<Tuple2<String, Integer>>> paperEventCntByFunction = new ConcurrentHashMap<>();

    private List<Paper> papersWithVariants;

    public StatsService() {
        log.info( "StatsService created" );
    }

    @PostConstruct
    public void init() {
        log.info( "StatsService init" );
        statsDAO = daoFactoryBean.getDAOFactory().getStatsDAO();

        Builder<Paper> papersWithVariantsBuilder = new ImmutableList.Builder<>();

        for ( Paper p : cacheService.listPapers() ) {

            int cnt = statsDAO.findTotalVariantsByPaperId( p.getId() );

            paperVariantCntCache.put( p.getId(), cnt );

            if ( cnt > 0 ) {
                papersWithVariantsBuilder.add( p );
            }

            paperEventCntCache.put( p.getId(), statsDAO.findTotalEventsByPaperId( p.getId() ) );

            List<Tuple2<String, Integer>> l = statsDAO.findTotalEventsByContextForPaperId( ( p.getId() ) );
            Collections.sort( l, TUPLE_COMPARE_T1 );
            paperEventCntByContext.put( p.getId(), ImmutableList.copyOf( l ) );

            l = statsDAO.findTotalEventsByCategoryForPaperId( ( p.getId() ) );
            Collections.sort( l, TUPLE_COMPARE_T1 );
            paperEventCntByFunction.put( p.getId(), ImmutableList.copyOf( l ) );
        }

        papersWithVariants = papersWithVariantsBuilder.build();

    }

    private static final Comparator<Tuple2<String, Integer>> TUPLE_COMPARE_T1 = new Comparator<Tuple2<String, Integer>>() {

        @Override
        public int compare( Tuple2<String, Integer> e1, Tuple2<String, Integer> e2 ) {

            return e1.getT1().compareTo( e2.getT1() );

        }

    };

    public List<Tuple2<String, Integer>> getEventCntByCategory( Integer paperId ) {
        return paperEventCntByFunction.get( paperId );
    }

    public List<Tuple2<String, Integer>> getEventCntByContext( Integer paperId ) {
        return paperEventCntByContext.get( paperId );
    }

    /**
     * Not Cached
     */
    public List<Tuple2<String, Integer>> getEventCntByCategory() {
        return statsDAO.findTotalEventsByCategory();
    }

    /**
     * Not Cached
     */
    public List<Tuple2<String, Integer>> getEventCntByContext() {
        return statsDAO.findTotalEventsByContext();
    }

    public Integer getVariantCntByPaperId( Integer paperId ) {
        if ( paperId == null ) {
            return null;
        }
        return paperVariantCntCache.get( paperId );
    }

    public Integer getEventCntByPaperId( Integer paperId ) {
        if ( paperId == null ) {
            return null;
        }
        return paperEventCntCache.get( paperId );
    }

    public int getPaperCnt() {
        return latestPaperCnt.get();
    }

    public int getVariantCnt() {
        return latestVariantCnt.get();
    }

    public int getEventCnt() {
        return latestEventCnt.get();
    }

    public int getSubjectCnt() {
        return latestSubjectCnt.get();
    }

    public List<Gene> getTopGenesByVariantCnt() {
        return latestTopGenesByVariantCnt.get();
    }

    public List<Gene> getTopGenesByEventCnt() {
        return latestTopGenesByEventCnt.get();
    }

    public List<Gene> getTopGenesByPaperCnt() {
        return latestTopGenesByPaperCnt.get();
    }

    public List<Paper> getPapersWithVariants() {
        return papersWithVariants;
    }

    private Supplier<List<Gene>> topGenesByVariantCntSupplier() {
        return new Supplier<List<Gene>>() {
            @Override
            public List<Gene> get() {
                log.info( "topGenesByVariantCntSupplier" );
                List<Integer> geneIds = statsDAO.findTopGenesByVariantCnt( TOP_N );

                List<Gene> genes = new ArrayList<>();

                for ( Integer geneId : geneIds ) {
                    genes.add( cacheService.getGeneById( geneId ) );
                }

                return genes;

            }
        };
    }

    private Supplier<List<Gene>> topGenesByEventCntSupplier() {
        return new Supplier<List<Gene>>() {
            @Override
            public List<Gene> get() {
                log.info( "topGenesByEventCntSupplier" );
                List<Integer> geneIds = statsDAO.findTopGenesByEventCnt( TOP_N );

                List<Gene> genes = new ArrayList<>();

                for ( Integer geneId : geneIds ) {
                    genes.add( cacheService.getGeneById( geneId ) );
                }

                return genes;

            }
        };
    }

    private Supplier<List<Gene>> topGenesByPaperCntSupplier() {
        return new Supplier<List<Gene>>() {
            @Override
            public List<Gene> get() {
                log.info( "topGenesByPaperCntSupplier" );
                List<Integer> geneIds = statsDAO.findTopGenesByPaperCnt( TOP_N );

                List<Gene> genes = new ArrayList<>();

                for ( Integer geneId : geneIds ) {
                    genes.add( cacheService.getGeneById( geneId ) );
                }

                return genes;

            }
        };
    }

    private Supplier<Integer> paperCntSupplier() {
        return new Supplier<Integer>() {
            @Override
            public Integer get() {
                log.info( "paperCntSupplier" );
                return statsDAO.findTotalPapersWithVariants();
            }
        };
    }

    private Supplier<Integer> variantCntSupplier() {
        return new Supplier<Integer>() {
            @Override
            public Integer get() {
                log.info( "variantCntSupplier" );
                return statsDAO.findTotalVariants();
            }
        };
    }

    private Supplier<Integer> eventCntSupplier() {
        return new Supplier<Integer>() {
            @Override
            public Integer get() {
                log.info( "eventCntSupplier" );
                return statsDAO.findTotalEvents();
            }
        };
    }

    private Supplier<Integer> subjectCntSupplier() {
        return new Supplier<Integer>() {
            @Override
            public Integer get() {
                log.info( "subjectCntSupplier" );
                return statsDAO.findTotalSubjects();
            }
        };
    }

    public Map<Integer, Integer> overlappingEventsBetweenPapers( Integer paper_id ) {
        return statsDAO.overlappingEventsBetweenPapers( paper_id );
    }

    public void setDaoFactoryBean( DAOFactoryBean daoFactoryBean ) {
        this.daoFactoryBean = daoFactoryBean;
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }

}
