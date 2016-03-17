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

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ubc.pavlab.ndb.BaseTest;
import ubc.pavlab.ndb.model.Paper;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class PaperServiceTest extends BaseTest {
    private static final Logger log = Logger.getLogger( PaperServiceTest.class );
    private static PaperService paperService;

    @BeforeClass
    public static void classSetup() {
        paperService = getMockPaperService();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @Test
    public void testFetchPaper1() {
        Paper res = paperService.fetchPaper( 7 );
        assertIsPaper1( res );
    }

    @Test
    public void testFetchPaper2() {
        Paper res = paperService.fetchPaper( "O'Roak" );
        assertIsPaper2( res );
    }

    @Test
    public void testListPapers() {
        List<Paper> res = paperService.listPapers();
        Assert.assertThat( res.size(), is( 4 ) );
    }

    private void assertIsPaper1( Paper e ) {
        Assert.assertThat( e, Matchers.notNullValue() );
        Assert.assertThat( e.getId(), Matchers.is( 7 ) );
        Assert.assertThat( e.getUrl(), Matchers.is( "https://dx.doi.org/10.1038/nature13908" ) );
        Assert.assertThat( e.getAuthor(), Matchers.is( "Iossifov" ) );
        Assert.assertThat( e.getPaperTable(), Matchers.is( "Supplementary Table 2: List of de novo mutations" ) );
        Assert.assertThat( e.getMutReporting(), Matchers.is( "De novo" ) );
        Assert.assertThat( e.getScope(), Matchers.is( "WES" ) );
        Assert.assertThat( e.isParents(), Matchers.is( false ) );
        Assert.assertThat( e.getCohort(), Matchers.nullValue() );
        Assert.assertThat( e.getCohortSource(), Matchers.is( "SSC" ) );
        Assert.assertThat( e.getCohortSize(), Matchers.is( 0 ) );
        Assert.assertThat( e.getReportedEffects(), Matchers.is(
                "3UTR, 5UTR, Frameshift, Missense, Codon Deletion, Codon Insertion, No Stop, Nonsense, No Start" ) );
        Assert.assertThat( e.getDoi(), Matchers.is( "10.1038/nature13908" ) );
        Assert.assertThat( e.getTitle(),
                Matchers.is( "The contribution of de novo coding mutations to autism spectrum disorder" ) );
        Assert.assertThat( e.getPaperKey(), Matchers.is( "Iossifov2" ) );
        Assert.assertThat( e.getPublisher(), Matchers.is( "Nature Publishing Group" ) );
        Assert.assertThat( e.getYear(), Matchers.is( "2014" ) );
        Assert.assertThat( e.getCases(), Matchers.is( "ASD" ) );
        Assert.assertThat( e.getCount(), Matchers.is( "2517 families" ) );
        Assert.assertThat( e.getDesign(), Matchers.is( "Simplex" ) );
    }

    private void assertIsPaper2( Paper e ) {
        Assert.assertThat( e, Matchers.notNullValue() );
        Assert.assertThat( e.getId(), Matchers.is( 16 ) );
        Assert.assertThat( e.getUrl(), Matchers.is( "https://dx.doi.org/10.1038/nature10989" ) );
        Assert.assertThat( e.getAuthor(), Matchers.is( "O'Roak" ) );
        Assert.assertThat( e.getPaperTable(),
                Matchers.is( "Supplementary Table 3: All 242 de novo point mutations found in 189 trios" ) );
        Assert.assertThat( e.getMutReporting(), Matchers.is( "De novo" ) );
        Assert.assertThat( e.getScope(), Matchers.is( "WES" ) );
        Assert.assertThat( e.isParents(), Matchers.is( false ) );
        Assert.assertThat( e.getCohort(), Matchers.nullValue() );
        Assert.assertThat( e.getCohortSource(), Matchers.is( "SSC" ) );
        Assert.assertThat( e.getCohortSize(), Matchers.is( 0 ) );
        Assert.assertThat( e.getReportedEffects(),
                Matchers.is( "Missense, Frameshift, Codon Deletion, Nonsense, Codon Insertion, Splice Site" ) );
        Assert.assertThat( e.getDoi(), Matchers.is( "10.1038/nature10989" ) );
        Assert.assertThat( e.getTitle(), Matchers
                .is( "Sporadic autism exomes reveal a highly interconnected protein network of de novo mutations" ) );
        Assert.assertThat( e.getPaperKey(), Matchers.is( "O'Roak" ) );
        Assert.assertThat( e.getPublisher(), Matchers.is( "Nature Publishing Group" ) );
        Assert.assertThat( e.getYear(), Matchers.is( "2012" ) );
        Assert.assertThat( e.getCases(), Matchers.is( "ASD" ) );
        Assert.assertThat( e.getCount(), Matchers.is( "209 trios" ) );
        Assert.assertThat( e.getDesign(), Matchers.is( "Simplex" ) );
    }
}
