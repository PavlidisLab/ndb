/*
 * The ndb project
 * 
 * Copyright (c) 2016 University of British Columbia
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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Sets;

import ubc.pavlab.ndb.BaseTest;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Paper;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class CacheServiceTest extends BaseTest {
    private static final Logger log = Logger.getLogger( CacheServiceTest.class );
    private static CacheService cacheService;

    @BeforeClass
    public static void classSetup() {
        cacheService = getMockCacheService();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @Test
    public void testSearchGeneBySymbol() {
        List<Gene> res = cacheService.searchGeneBySymbol( GENE1_SYMBOL );
        Assert.assertEquals( 1, res.size() );
        assertIsGene1( res.get( 0 ) );

        res = cacheService.searchGeneBySymbol( "AD" );
        Assert.assertEquals( 3, res.size() );

        Set<String> symbols = Sets.newHashSet( "ADAM33", "ADNP", "ADSS" );

        for ( Gene g : res ) {
            Assert.assertTrue( symbols.contains( g.getSymbol() ) );
            symbols.remove( g.getSymbol() );
        }
    }

    @Test
    public void testGetGeneForExactSymbol() {
        Gene res = cacheService.getGeneForExactSymbol( GENE1_SYMBOL );
        assertIsGene1( res );

        res = cacheService.getGeneForExactSymbol( GENE2_SYMBOL );
        assertIsGene2( res );
    }

    @Test
    public void testGetGeneById() {
        Gene res = cacheService.getGeneById( GENE1_ID );
        assertIsGene1( res );

        res = cacheService.getGeneById( GENE2_ID );
        assertIsGene2( res );
    }

    @Test
    public void testSearchPaperByAuthor() {
        List<Paper> res = cacheService.searchPaperByAuthor( PAPER1_AUTHOR );
        Assert.assertEquals( 1, res.size() );
        assertIsPaper1( res.get( 0 ) );

        res = cacheService.searchPaperByAuthor( PAPER2_AUTHOR );
        Assert.assertEquals( 1, res.size() );
        assertIsPaper2( res.get( 0 ) );

        res = cacheService.searchPaperByAuthor( "R" );
        Assert.assertEquals( 0, res.size() );
    }

    @Test
    public void testGetPaperForExactAuthor() {
        Paper res = cacheService.getPaperForExactAuthor( PAPER1_AUTHOR );
        assertIsPaper1( res );

        res = cacheService.getPaperForExactAuthor( PAPER2_AUTHOR );
        assertIsPaper2( res );

    }

    @Test
    public void testGetPaperById() {
        Paper res = cacheService.getPaperById( PAPER1_ID );
        assertIsPaper1( res );

        res = cacheService.getPaperById( PAPER2_ID );
        assertIsPaper2( res );
    }

    @Test
    public void testListPapers() {
        Collection<Paper> res = cacheService.listPapers();
        Assert.assertEquals( 4, res.size() );
    }

}
