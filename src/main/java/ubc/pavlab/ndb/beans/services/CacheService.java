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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Paper;

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

    @ManagedProperty("#{geneService}")
    private GeneService geneService;

    @ManagedProperty("#{paperService}")
    private PaperService paperService;

    //
    private RadixTree<Gene> geneTreeBySymbol = new ConcurrentRadixTree<Gene>( new DefaultCharArrayNodeFactory() );
    private Map<Integer, Gene> geneCache = new ConcurrentHashMap<>();
    private RadixTree<Paper> paperTreeByAuthor = new ConcurrentRadixTree<Paper>( new DefaultCharArrayNodeFactory() );
    private Map<Integer, Paper> paperCache = new ConcurrentHashMap<>();

    /**
     * 
     */
    public CacheService() {
        log.info( "CacheService created" );
    }

    @PostConstruct
    public void init() {
        log.info( "CacheService init" );

        for ( Gene g : geneService.listGenes() ) {
            geneTreeBySymbol.put( g.getSymbol().toUpperCase(), g );
            geneCache.put( g.getGeneId(), g );
        }

        log.info( "Gene Cache loaded with " + geneCache.size() + " genes." );

        for ( Paper p : paperService.listPapers() ) {
            paperTreeByAuthor.put( p.getAuthor(), p );
            paperCache.put( p.getId(), p );
            ;
        }

        log.info( "Paper Cache loaded with " + paperCache.size() + " papers." );

    }

    public List<Gene> searchGeneBySymbol( String query ) {
        Iterable<Gene> iter = geneTreeBySymbol.getValuesForClosestKeys( query.toUpperCase() );
        return Lists.newArrayList( iter );
    }

    public Gene getGeneForExactSymbol( String symbol ) {
        return geneTreeBySymbol.getValueForExactKey( symbol.toUpperCase() );
    }

    public Gene getGeneById( Integer id ) {
        return geneCache.get( id );
    }

    public List<Paper> searchPaperByAuthor( String query ) {
        Iterable<Paper> iter = paperTreeByAuthor.getValuesForClosestKeys( query.toUpperCase() );
        return Lists.newArrayList( iter );
    }

    public Paper getPaperForExactAuthor( String author ) {
        return paperTreeByAuthor.getValueForExactKey( author.toUpperCase() );
    }

    public Paper getPaperById( Integer id ) {
        return paperCache.get( id );
    }

    public Collection<Paper> listPapers() {
        return new ArrayList<>( paperCache.values() );
    }

    public void setDaoFactoryBean( DAOFactoryBean daoFactoryBean ) {
        this.daoFactoryBean = daoFactoryBean;
    }

    public void setGeneService( GeneService geneService ) {
        this.geneService = geneService;
    }

    public void setPaperService( PaperService paperService ) {
        this.paperService = paperService;
    }
}
