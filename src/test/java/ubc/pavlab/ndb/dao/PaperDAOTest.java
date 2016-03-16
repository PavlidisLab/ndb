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

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ubc.pavlab.ndb.BaseTest;
import ubc.pavlab.ndb.model.dto.PaperDTO;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class PaperDAOTest extends BaseTest {

    private static final Logger log = Logger.getLogger( PaperDAOTest.class );
    private static PaperDAO paperDAO;

    @BeforeClass
    public static void classSetup() {
        paperDAO = daoFactory.getPaperDAO();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @Test
    public void testFind1() {
        PaperDTO dto = paperDAO.find( 18 );
        Assert.assertThat( dto, Matchers.notNullValue() );
        Assert.assertThat( dto.getId(), Matchers.is( 18 ) );
        Assert.assertThat( dto.getUrl(), Matchers.nullValue() );
        Assert.assertThat( dto.getAuthor(), Matchers.is( "De Rubeis" ) );
        Assert.assertThat( dto.getPaper_table(),
                Matchers.is( "Supplementary Table 3, Willsey et al. 2013 (Table S1)" ) );
        Assert.assertThat( dto.getMut_reporting(), Matchers.is( "De novo, inherited" ) );
        Assert.assertThat( dto.getScope(), Matchers.is( "WES" ) );
        Assert.assertThat( dto.isParents(), Matchers.is( false ) );
        Assert.assertThat( dto.getCohort(), Matchers.nullValue() );
        Assert.assertThat( dto.getCohort_source(),
                Matchers.is( "ARRA, BAC, ISMMS, SSC, UK 10K,  Boston Children's Hospital, Goethe-Universit" ) );
        Assert.assertThat( dto.getCohort_size(), Matchers.is( 0 ) );
        Assert.assertThat( dto.getCount(), Matchers.is( "3871 cases" ) );
        Assert.assertThat( dto.getReported_effects(),
                Matchers.is( "Missense, Frameshift, Nonsense, Splice Site, Codon Deletion, New Start" ) );
        Assert.assertThat( dto.getDOI(), Matchers.is( "10.1038/nature13772" ) );
        Assert.assertThat( dto.getKey(), Matchers.is( "DeRubeis" ) );
        Assert.assertThat( dto.getYear(), Matchers.is( "2014" ) );
        Assert.assertThat( dto.getTitle(),
                Matchers.is( "Synaptic, transcriptional and chromatin genes disrupted in autism" ) );
        Assert.assertThat( dto.getCases(), Matchers.is( "ASD" ) );
        Assert.assertThat( dto.getDesign(), Matchers.is( "Simplex/Case-control" ) );
        Assert.assertThat( dto.getPublisher(), Matchers.is( "Nature Publishing Group" ) );
    }

    @Test
    public void testFind2() {
        PaperDTO dto = paperDAO.find( "Iossifov2" );
        Assert.assertThat( dto, Matchers.notNullValue() );
        Assert.assertThat( dto.getId(), Matchers.is( 7 ) );
        Assert.assertThat( dto.getUrl(), Matchers.nullValue() );
        Assert.assertThat( dto.getAuthor(), Matchers.is( "Iossifov" ) );
        Assert.assertThat( dto.getPaper_table(), Matchers.is( "Supplementary Table 2: List of de novo mutations" ) );
        Assert.assertThat( dto.getMut_reporting(), Matchers.is( "De novo" ) );
        Assert.assertThat( dto.getScope(), Matchers.is( "WES" ) );
        Assert.assertThat( dto.isParents(), Matchers.is( false ) );
        Assert.assertThat( dto.getCohort(), Matchers.nullValue() );
        Assert.assertThat( dto.getCohort_source(), Matchers.is( "SSC" ) );
        Assert.assertThat( dto.getCohort_size(), Matchers.is( 0 ) );
        Assert.assertThat( dto.getCount(), Matchers.is( "2517 families" ) );
        Assert.assertThat( dto.getReported_effects(), Matchers.is(
                "3UTR, 5UTR, Frameshift, Missense, Codon Deletion, Codon Insertion, No Stop, Nonsense, No Start" ) );
        Assert.assertThat( dto.getDOI(), Matchers.is( "10.1038/nature13908" ) );
        Assert.assertThat( dto.getKey(), Matchers.is( "Iossifov2" ) );
        Assert.assertThat( dto.getYear(), Matchers.is( "2014" ) );
        Assert.assertThat( dto.getTitle(),
                Matchers.is( "The contribution of de novo coding mutations to autism spectrum disorder" ) );
        Assert.assertThat( dto.getCases(), Matchers.is( "ASD" ) );
        Assert.assertThat( dto.getDesign(), Matchers.is( "Simplex" ) );
        Assert.assertThat( dto.getPublisher(), Matchers.is( "Nature Publishing Group" ) );
    }

    @Test
    public void testList() {
        List<PaperDTO> dtos = paperDAO.list();
        Assert.assertThat( dtos.size(), is( 4 ) );
    }

}
