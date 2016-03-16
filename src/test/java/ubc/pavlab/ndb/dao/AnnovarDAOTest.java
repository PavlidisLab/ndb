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

import com.google.common.collect.Lists;

import ubc.pavlab.ndb.BaseTest;
import ubc.pavlab.ndb.model.dto.AnnovarDTO;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class AnnovarDAOTest extends BaseTest {
    private static final Logger log = Logger.getLogger( AnnovarDAOTest.class );
    private static AnnovarDAO annovarDAO;

    @BeforeClass
    public static void classSetup() {
        annovarDAO = daoFactory.getAnnovarDAO();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @Test
    public void testFind() {
        AnnovarDTO dto = annovarDAO.find( 120 );
        assertIsAnnovar1( dto );
    }

    @Test
    public void testFindByVariantId1() {
        AnnovarDTO dto = annovarDAO.findByVariantId( 120 );
        assertIsAnnovar1( dto );
    }

    @Test
    public void testFindByVariantId2() {
        List<Integer> ids = Lists.newArrayList( 174, 226, 273, 275, 401, 409, 605, 832, 1666 );
        List<AnnovarDTO> dtos = annovarDAO.findByVariantId( ids );

        Assert.assertThat( dtos.size(), is( 9 ) );

        ids = Lists.newArrayList( 1846, 2798, 3444, 3481, 3536, 3722, 9999999, 888888, 777777 );
        dtos = annovarDAO.findByVariantId( ids );

        Assert.assertThat( dtos.size(), is( 6 ) );

    }

    @Test
    public void testList() {
        List<AnnovarDTO> dtos = annovarDAO.list();
        Assert.assertThat( dtos.size(), is( 22 ) );
    }

    private void assertIsAnnovar1( AnnovarDTO dto ) {
        Assert.assertThat( dto, Matchers.notNullValue() );
        Assert.assertThat( dto.getId(), Matchers.is( 120 ) );
        Assert.assertThat( dto.getVariantId(), Matchers.is( 120 ) );
        Assert.assertThat( dto.getGenomicSuperDups(), Matchers.nullValue() );
        Assert.assertThat( dto.getEsp6500siv2All(), Matchers.is( 0.0 ) );
        Assert.assertThat( dto.getOctAll1000g2014(), Matchers.is( 0.0 ) );
        Assert.assertThat( dto.getOctAfr1000g2014(), Matchers.is( 0.0 ) );
        Assert.assertThat( dto.getOctEas1000g2014(), Matchers.is( 0.0 ) );
        Assert.assertThat( dto.getOctEur1000g2014(), Matchers.is( 0.0 ) );
        Assert.assertThat( dto.getSnp138(), Matchers.nullValue() );
        Assert.assertThat( dto.getSiftScore(), Matchers.is( 0.01 ) );
        Assert.assertThat( dto.getSiftPred(), Matchers.is( "D" ) );
        Assert.assertThat( dto.getPolyphen2hdivScore(), Matchers.is( 1.0 ) );
        Assert.assertThat( dto.getPolyphen2hdivPred(), Matchers.is( "D" ) );
        Assert.assertThat( dto.getPolyphen2hvarScore(), Matchers.is( 0.997 ) );
        Assert.assertThat( dto.getPolyphen2hvarPred(), Matchers.is( "D" ) );
        Assert.assertThat( dto.getLrtScore(), Matchers.is( 0.0 ) );
        Assert.assertThat( dto.getLrtPred(), Matchers.is( "D" ) );
        Assert.assertThat( dto.getMutationTasterScore(), Matchers.is( 1.0 ) );
        Assert.assertThat( dto.getMutationTasterPred(), Matchers.is( "D" ) );
        Assert.assertThat( dto.getMutationAssessorScore(), Matchers.is( 2.745 ) );
        Assert.assertThat( dto.getMutationAssessorPred(), Matchers.is( "M" ) );
        Assert.assertThat( dto.getFathmmScore(), Matchers.is( 2.55 ) );
        Assert.assertThat( dto.getFathmmPred(), Matchers.is( "T" ) );
        Assert.assertThat( dto.getRadialSVMScore(), Matchers.is( -0.929 ) );
        Assert.assertThat( dto.getRadialSVMPred(), Matchers.is( "T" ) );
        Assert.assertThat( dto.getLrScore(), Matchers.is( 0.114 ) );
        Assert.assertThat( dto.getLrPred(), Matchers.is( "T" ) );
        Assert.assertThat( dto.getVest3Score(), Matchers.is( 0.838 ) );
        Assert.assertThat( dto.getCaddRaw(), Matchers.is( 4.798 ) );
        Assert.assertThat( dto.getCaddPhred(), Matchers.is( 27.1 ) );
        Assert.assertThat( dto.getGerpRs(), Matchers.is( 4.8 ) );
        Assert.assertThat( dto.getPhyloP46wayPlacental(), Matchers.is( 1.915 ) );
        Assert.assertThat( dto.getPhyloP100wayVertebrate(), Matchers.is( 4.613 ) );
        Assert.assertThat( dto.getSiphy29wayLogOdds(), Matchers.is( 13.222 ) );
        Assert.assertThat( dto.getExac03(), Matchers.is( 0.0 ) );
        Assert.assertThat( dto.getClinvar20150629(), Matchers.nullValue() );
    }

}
