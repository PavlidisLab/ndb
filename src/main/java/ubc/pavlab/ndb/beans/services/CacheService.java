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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.dao.CacheDAO;
import ubc.pavlab.ndb.utility.Tuples.Tuple2;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class CacheService implements Serializable {

    private static final long serialVersionUID = 8686650216735635928L;

    private static final Logger log = Logger.getLogger( CacheService.class );

    @ManagedProperty("#{daoFactoryBean}")
    private DAOFactoryBean daoFactoryBean;

    private CacheDAO cacheDAO;

    private RadixTree<Integer> geneSymbolToIdTree = new ConcurrentRadixTree<Integer>(
            new DefaultCharArrayNodeFactory() );
    private RadixTree<Integer> paperAuthorToIdTree = new ConcurrentRadixTree<Integer>(
            new DefaultCharArrayNodeFactory() );

    /**
     * 
     */
    public CacheService() {
        log.info( "CacheService created" );
    }

    @PostConstruct
    public void init() {
        log.info( "CacheService init" );
        cacheDAO = daoFactoryBean.getDAOFactory().getCacheDAO();
        List<Tuple2<Integer, String>> symbolIdTuples = cacheDAO.listSymbolId();
        for ( Tuple2<Integer, String> tuple2 : symbolIdTuples ) {
            geneSymbolToIdTree.put( tuple2.getT2().toUpperCase(), tuple2.getT1() );
        }

        List<Tuple2<Integer, String>> authorIdTuples = cacheDAO.listAuthorId();
        for ( Tuple2<Integer, String> tuple2 : authorIdTuples ) {
            paperAuthorToIdTree.put( tuple2.getT2().toUpperCase(), tuple2.getT1() );
        }
    }

    public List<KeyValuePair<Integer>> searchGeneIdBySymbol( String query ) {
        Iterable<KeyValuePair<Integer>> iter = geneSymbolToIdTree.getKeyValuePairsForClosestKeys( query.toUpperCase() );
        return Lists.newArrayList( iter );
    }

    public Integer getGeneIdForExactSymbol( String symbol ) {
        return geneSymbolToIdTree.getValueForExactKey( symbol.toUpperCase() );
    }

    public List<KeyValuePair<Integer>> searchPaperIdByAuthor( String query ) {
        Iterable<KeyValuePair<Integer>> iter = paperAuthorToIdTree
                .getKeyValuePairsForClosestKeys( query.toUpperCase() );
        return Lists.newArrayList( iter );
    }

    public Integer getPaperIdForExactAuthor( String author ) {
        return paperAuthorToIdTree.getValueForExactKey( author.toUpperCase() );
    }

    public void setDaoFactoryBean( DAOFactoryBean daoFactoryBean ) {
        this.daoFactoryBean = daoFactoryBean;
    }
}
