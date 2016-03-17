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

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ubc.pavlab.ndb.BaseTest;
import ubc.pavlab.ndb.model.Annovar;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class AnnovarServiceTest extends BaseTest {
    private static final Logger log = Logger.getLogger( AnnovarServiceTest.class );
    private static AnnovarService annovarService;

    @BeforeClass
    public static void classSetup() {
        annovarService = getMockAnnovarService();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @Test
    public void testFetchById() {
        Annovar res = annovarService.fetchById( 120 );
        assertIsAnnovar1( res );
    }

    @Test
    public void testFetchByVariantId() {
        Annovar res = annovarService.fetchById( 120 );
        assertIsAnnovar1( res );
    }

    private void assertIsAnnovar1( Annovar ent ) {
        Assert.assertThat( ent, Matchers.notNullValue() );
        Assert.assertThat( ent.getId(), Matchers.is( 120 ) );
        Assert.assertThat( ent.getVariantId(), Matchers.is( 120 ) );
        Assert.assertThat( ent.getGenomicSuperDups(), Matchers.nullValue() );
        Assert.assertThat( ent.getEsp6500siv2All(), Matchers.is( 0.0 ) );
        Assert.assertThat( ent.getOctAll1000g2014(), Matchers.is( 0.0 ) );
        Assert.assertThat( ent.getOctAfr1000g2014(), Matchers.is( 0.0 ) );
        Assert.assertThat( ent.getOctEas1000g2014(), Matchers.is( 0.0 ) );
        Assert.assertThat( ent.getOctEur1000g2014(), Matchers.is( 0.0 ) );
        Assert.assertThat( ent.getSnp138(), Matchers.nullValue() );
        Assert.assertThat( ent.getSiftScore(), Matchers.is( 0.01 ) );
        Assert.assertThat( ent.getSiftPred(), Matchers.is( "D" ) );
        Assert.assertThat( ent.getPolyphen2hdivScore(), Matchers.is( 1.0 ) );
        Assert.assertThat( ent.getPolyphen2hdivPred(), Matchers.is( "D" ) );
        Assert.assertThat( ent.getPolyphen2hvarScore(), Matchers.is( 0.997 ) );
        Assert.assertThat( ent.getPolyphen2hvarPred(), Matchers.is( "D" ) );
        Assert.assertThat( ent.getLrtScore(), Matchers.is( 0.0 ) );
        Assert.assertThat( ent.getLrtPred(), Matchers.is( "D" ) );
        Assert.assertThat( ent.getMutationTasterScore(), Matchers.is( 1.0 ) );
        Assert.assertThat( ent.getMutationTasterPred(), Matchers.is( "D" ) );
        Assert.assertThat( ent.getMutationAssessorScore(), Matchers.is( 2.745 ) );
        Assert.assertThat( ent.getMutationAssessorPred(), Matchers.is( "M" ) );
        Assert.assertThat( ent.getFathmmScore(), Matchers.is( 2.55 ) );
        Assert.assertThat( ent.getFathmmPred(), Matchers.is( "T" ) );
        Assert.assertThat( ent.getRadialSVMScore(), Matchers.is( -0.929 ) );
        Assert.assertThat( ent.getRadialSVMPred(), Matchers.is( "T" ) );
        Assert.assertThat( ent.getLrScore(), Matchers.is( 0.114 ) );
        Assert.assertThat( ent.getLrPred(), Matchers.is( "T" ) );
        Assert.assertThat( ent.getVest3Score(), Matchers.is( 0.838 ) );
        Assert.assertThat( ent.getCaddRaw(), Matchers.is( 4.798 ) );
        Assert.assertThat( ent.getCaddPhred(), Matchers.is( 27.1 ) );
        Assert.assertThat( ent.getGerpRs(), Matchers.is( 4.8 ) );
        Assert.assertThat( ent.getPhyloP46wayPlacental(), Matchers.is( 1.915 ) );
        Assert.assertThat( ent.getPhyloP100wayVertebrate(), Matchers.is( 4.613 ) );
        Assert.assertThat( ent.getSiphy29wayLogOdds(), Matchers.is( 13.222 ) );
        Assert.assertThat( ent.getExac03(), Matchers.is( 0.0 ) );
        Assert.assertThat( ent.getClinvar20150629(), Matchers.nullValue() );
    }
}
