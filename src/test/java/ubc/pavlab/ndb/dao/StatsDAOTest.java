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

package ubc.pavlab.ndb.dao;

import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ubc.pavlab.ndb.BaseTest;
import ubc.pavlab.ndb.utility.Tuples;
import ubc.pavlab.ndb.utility.Tuples.Tuple2;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class StatsDAOTest extends BaseTest {
    private static final Logger log = Logger.getLogger( StatsDAOTest.class );
    private static StatsDAO statsDAO;

    @BeforeClass
    public static void classSetup() {
        statsDAO = daoFactory.getStatsDAO();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @Test
    public void findTotalPapers() {
        int res = statsDAO.findTotalPapers();
        Assert.assertThat( res, Matchers.is( 4 ) );
    }

    @Test
    public void findTotalVariantsByPaperId() {
        int res = statsDAO.findTotalVariantsByPaperId( 7 );
        Assert.assertThat( res, Matchers.is( 8 ) );
        res = statsDAO.findTotalVariantsByPaperId( 16 );
        Assert.assertThat( res, Matchers.is( 1 ) );
        res = statsDAO.findTotalVariantsByPaperId( 18 );
        Assert.assertThat( res, Matchers.is( 13 ) );
    }

    @Test
    public void findTotalEventsByPaperId() {
        int res = statsDAO.findTotalEventsByPaperId( 7 );
        Assert.assertThat( res, Matchers.is( 8 ) );
        res = statsDAO.findTotalEventsByPaperId( 16 );
        Assert.assertThat( res, Matchers.is( 1 ) );
        res = statsDAO.findTotalEventsByPaperId( 18 );
        Assert.assertThat( res, Matchers.is( 13 ) );
    }

    @Test
    public void findTotalVariants() {
        int res = statsDAO.findTotalVariants();
        Assert.assertThat( res, Matchers.is( 22 ) );
    }

    @Test
    public void findTotalEvents() {
        int res = statsDAO.findTotalEvents();
        Assert.assertThat( res, Matchers.is( 19 ) );
    }

    @Test
    public void findTotalSubjects() {
        int res = statsDAO.findTotalSubjects();
        Assert.assertThat( res, Matchers.is( 19 ) );
    }

    @Test
    public void findTotalPapersWithVariants() {
        int res = statsDAO.findTotalPapersWithVariants();
        Assert.assertThat( res, Matchers.is( 3 ) );
    }

    @Test
    public void findTopGenesByVariantCnt() {
        List<Integer> res = statsDAO.findTopGenesByVariantCnt( 5 );
        Assert.assertThat( res.size(), is( 5 ) );
        Assert.assertThat( res, Matchers.containsInAnyOrder( 10588, 23258, 26040, 80332, 100528021 ) );
    }

    @Test
    @Ignore
    public void findTopGenesByEventCnt() {
        //TODO needs better data all have 1
        List<Integer> res = statsDAO.findTopGenesByEventCnt( 5 );
        //        Assert.assertThat( res.size(), is( 5 ) );
        //        Assert.assertThat( res, Matchers.containsInAnyOrder( 10588, 23258, 26040, 80332, 100528021 ) );
    }

    @Test
    public void findTopGenesByPaperCnt() {
        List<Integer> res = statsDAO.findTopGenesByPaperCnt( 4 );
        Assert.assertThat( res.size(), is( 4 ) );
        Assert.assertThat( res, Matchers.containsInAnyOrder( 10588, 26040, 80332, 100528021 ) ); // and one more
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findTotalVariantsByContextForPaperId() {
        List<Tuple2<String, Integer>> res = statsDAO.findTotalVariantsByContextForPaperId( 7 );
        Assert.assertThat( res.size(), is( 4 ) );
        Assert.assertThat( res, Matchers.containsInAnyOrder( Tuples.tuple2( "exonic", 5 ),
                Tuples.tuple2( "UTR5", 1 ),
                Tuples.tuple2( "ncRNA_intronic", 1 ),
                Tuples.tuple2( "intergenic", 1 ) ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findTotalEventsByContextForPaperId() {
        List<Tuple2<String, Integer>> res = statsDAO.findTotalVariantsByContextForPaperId( 18 );
        Assert.assertThat( res.size(), is( 7 ) );
        Assert.assertThat( res, Matchers.containsInAnyOrder( Tuples.tuple2( "exonic", 7 ),
                Tuples.tuple2( "UTR5", 1 ),
                Tuples.tuple2( "ncRNA_exonic", 1 ),
                Tuples.tuple2( "intronic", 1 ),
                Tuples.tuple2( "exonic;splicing", 1 ),
                Tuples.tuple2( "splicing", 1 ),
                Tuples.tuple2( "UTR3", 1 ) ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findTotalVariantsByCategoryForPaperId() {
        List<Tuple2<String, Integer>> res = statsDAO.findTotalVariantsByCategoryForPaperId( 7 );
        Assert.assertThat( res.size(), is( 7 ) );
        Assert.assertThat( res, Matchers.containsInAnyOrder( Tuples.tuple2( "other", 2 ),
                Tuples.tuple2( "nonframeshift insertion", 1 ),
                Tuples.tuple2( "nonframeshift substitution", 1 ),
                Tuples.tuple2( "nonsynonymous SNV", 1 ),
                Tuples.tuple2( "stopgain", 1 ),
                Tuples.tuple2( "stoploss", 1 ),
                Tuples.tuple2( "unknown", 1 ) ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findTotalEventsByCategoryForPaperId() {
        List<Tuple2<String, Integer>> res = statsDAO.findTotalEventsByCategoryForPaperId( 18 );
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
    public void findTotalEventsByCategory() {
        List<Tuple2<String, Integer>> res = statsDAO.findTotalEventsByCategory();
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
    public void findTotalEventsByContext() {
        List<Tuple2<String, Integer>> res = statsDAO.findTotalEventsByContext();
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

    @Test
    public void overlappingEventsBetweenPapers() {
        Map<Integer, Integer> res = statsDAO.overlappingEventsBetweenPapers( 7 );
        Assert.assertThat( res.size(), is( 3 ) );
        Assert.assertThat( res.get( 16 ), is( 1 ) );
        Assert.assertThat( res.get( 18 ), is( 2 ) );
        Assert.assertThat( res.get( 7 ), is( 8 ) );
    }

}
