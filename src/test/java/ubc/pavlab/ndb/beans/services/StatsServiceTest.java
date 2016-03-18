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

import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Sets;

import ubc.pavlab.ndb.BaseTest;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.utility.Tuples;
import ubc.pavlab.ndb.utility.Tuples.Tuple2;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class StatsServiceTest extends BaseTest {
    private static final Logger log = Logger.getLogger( StatsServiceTest.class );
    private static StatsService statsService;

    @BeforeClass
    public static void classSetup() {
        statsService = getMockStatsService();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetEventCntByCategory1() {
        List<Tuple2<String, Integer>> res = statsService.getEventCntByCategory();
        Assert.assertThat( res.size(), is( 13 ) );
        Assert.assertThat( res, Matchers.containsInAnyOrder( Tuples.tuple2( "other", 6 ),
                Tuples.tuple2( "frameshift deletion", 1 ),
                Tuples.tuple2( "frameshift insertion", 1 ),
                Tuples.tuple2( "frameshift substitution", 1 ),
                Tuples.tuple2( "nonframeshift deletion", 1 ),
                Tuples.tuple2( "nonframeshift insertion", 1 ),
                Tuples.tuple2( "nonframeshift substitution", 1 ),
                Tuples.tuple2( "nonsynonymous SNV", 3 ),
                Tuples.tuple2( "splicing", 1 ),
                Tuples.tuple2( "stopgain", 1 ),
                Tuples.tuple2( "stoploss", 1 ),
                Tuples.tuple2( "synonymous SNV", 1 ),
                Tuples.tuple2( "unknown", 1 ) ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetEventCntByCategory2() {
        List<Tuple2<String, Integer>> res = statsService.getEventCntByCategory( 18 );
        Assert.assertThat( res.size(), is( 9 ) );
        Assert.assertThat( res, Matchers.containsInAnyOrder( Tuples.tuple2( "other", 4 ),
                Tuples.tuple2( "frameshift deletion", 1 ),
                Tuples.tuple2( "frameshift insertion", 1 ),
                Tuples.tuple2( "nonframeshift deletion", 1 ),
                Tuples.tuple2( "nonsynonymous SNV", 2 ),
                Tuples.tuple2( "splicing", 1 ),
                Tuples.tuple2( "stopgain", 1 ),
                Tuples.tuple2( "stoploss", 1 ),
                Tuples.tuple2( "synonymous SNV", 1 ) ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetEventCntByContext1() {
        List<Tuple2<String, Integer>> res = statsService.getEventCntByContext();
        Assert.assertThat( res.size(), is( 9 ) );
        Assert.assertThat( res, Matchers.containsInAnyOrder( Tuples.tuple2( "exonic", 10 ),
                Tuples.tuple2( "exonic;splicing", 1 ),
                Tuples.tuple2( "intergenic", 1 ),
                Tuples.tuple2( "intronic", 1 ),
                Tuples.tuple2( "ncRNA_exonic", 1 ),
                Tuples.tuple2( "ncRNA_intronic", 1 ),
                Tuples.tuple2( "splicing", 1 ),
                Tuples.tuple2( "UTR3", 1 ),
                Tuples.tuple2( "UTR5", 2 ) ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetEventCntByContext2() {
        List<Tuple2<String, Integer>> res = statsService.getEventCntByContext( 18 );
        Assert.assertThat( res.size(), is( 7 ) );
        Assert.assertThat( res, Matchers.containsInAnyOrder( Tuples.tuple2( "exonic", 7 ),
                Tuples.tuple2( "UTR5", 1 ),
                Tuples.tuple2( "ncRNA_exonic", 1 ),
                Tuples.tuple2( "intronic", 1 ),
                Tuples.tuple2( "exonic;splicing", 1 ),
                Tuples.tuple2( "splicing", 1 ),
                Tuples.tuple2( "UTR3", 1 ) ) );
    }

    @Test
    public void testGetVariantCntByPaperId() {
        Integer res = statsService.getVariantCntByPaperId( 7 );
        Assert.assertThat( res, Matchers.is( 8 ) );
        res = statsService.getVariantCntByPaperId( 16 );
        Assert.assertThat( res, Matchers.is( 1 ) );
        res = statsService.getVariantCntByPaperId( 18 );
        Assert.assertThat( res, Matchers.is( 13 ) );
    }

    @Test
    public void testGetEventCntByPaperId() {
        int res = statsService.getEventCntByPaperId( 7 );
        Assert.assertThat( res, Matchers.is( 8 ) );
        res = statsService.getEventCntByPaperId( 16 );
        Assert.assertThat( res, Matchers.is( 1 ) );
        res = statsService.getEventCntByPaperId( 18 );
        Assert.assertThat( res, Matchers.is( 13 ) );
    }

    @Test
    public void testGetPaperCnt() {
        int res = statsService.getPaperCnt();
        Assert.assertThat( res, Matchers.is( 3 ) );
    }

    @Test
    public void testGetVariantCnt() {
        int res = statsService.getVariantCnt();
        Assert.assertThat( res, Matchers.is( 22 ) );
    }

    @Test
    public void testGetEventCnt() {
        int res = statsService.getEventCnt();
        Assert.assertThat( res, Matchers.is( 19 ) );
    }

    @Test
    public void testGetSubjectCnt() {
        int res = statsService.getSubjectCnt();
        Assert.assertThat( res, Matchers.is( 19 ) );
    }

    @Test
    public void testGetTopGenesByVariantCnt() {
        List<Gene> res = statsService.getTopGenesByVariantCnt();
        Assert.assertThat( res.size(), is( 10 ) );

        Set<Integer> correctIds = Sets.newHashSet( 10588, 23258, 26040, 80332, 100528021 );

        Set<Integer> ids = Sets.newHashSet();
        for ( Gene g : res ) {
            ids.add( g.getGeneId() );
        }

        Assert.assertTrue( ids.containsAll( correctIds ) );

    }

    @Test
    @Ignore
    public void testGetTopGenesByEventCnt() {
        //TODO needs better data all have 1
        List<Gene> res = statsService.getTopGenesByEventCnt();
        //        Assert.assertThat( res.size(), is( 5 ) );
        //
        //        Set<Integer> ids = Sets.newHashSet( 10588, 23258, 26040, 80332, 100528021 );
        //
        //        for ( Gene g : res ) {
        //            Assert.assertTrue( ids.contains( g.getGeneId() ) );
        //            ids.remove( g.getGeneId() );
        //        }
    }

    @Test
    public void testGetTopGenesByPaperCnt() {
        List<Gene> res = statsService.getTopGenesByPaperCnt();
        Assert.assertThat( res.size(), is( 10 ) );

        Set<Integer> correctIds = Sets.newHashSet( 10588, 26040, 80332, 100528021 );

        Set<Integer> ids = Sets.newHashSet();
        for ( Gene g : res ) {
            ids.add( g.getGeneId() );
        }

        Assert.assertTrue( ids.containsAll( correctIds ) );

    }

    @Test
    public void testGetPapersWithVariants() {
        List<Paper> res = statsService.getPapersWithVariants();
        Assert.assertThat( res.size(), Matchers.is( 3 ) );
    }

    @Test
    public void testOverlappingEventsBetweenPapers() {
        Map<Integer, Integer> res = statsService.overlappingEventsBetweenPapers( 7 );
        Assert.assertThat( res.size(), is( 3 ) );
        Assert.assertThat( res.get( 16 ), is( 1 ) );
        Assert.assertThat( res.get( 18 ), is( 2 ) );
        Assert.assertThat( res.get( 7 ), is( 8 ) );
    }

    // TODO Test memoization expirations

}
