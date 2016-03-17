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

import com.google.common.collect.Sets;

import ubc.pavlab.ndb.BaseTest;
import ubc.pavlab.ndb.model.Gene;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class GeneServiceTest extends BaseTest {
    private static final Logger log = Logger.getLogger( GeneServiceTest.class );
    private static GeneService geneService;

    @BeforeClass
    public static void classSetup() {
        geneService = getMockGeneService();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @Test
    public void testFetchGene1() {
        Gene res = geneService.fetchGene( 13 );
        assertIsGene1( res );
    }

    @Test
    public void testFetchGene2() {
        Gene res = geneService.fetchGene( "ACACB" );
        assertIsGene2( res );
    }

    @Test
    public void testListGenes() {
        List<Gene> res = geneService.listGenes();
        Assert.assertThat( res.size(), is( 19 ) );
    }

    private void assertIsGene1( Gene g ) {
        Assert.assertThat( g, Matchers.notNullValue() );
        Assert.assertThat( g.getGeneId(), Matchers.is( 13 ) );
        Assert.assertThat( g.getSymbol(), Matchers.is( "AADAC" ) );
        Assert.assertEquals( g.getSynonyms(), Sets.newHashSet( "CES5A1", "DAC" ) );
        Assert.assertEquals( g.getXrefs(), Sets.newHashSet( "MIM:600338", "HGNC:HGNC:17", "Ensembl:ENSG00000114771",
                "HPRD:02640", "Vega:OTTHUMG00000159876" ) );
        Assert.assertThat( g.getChromose(), Matchers.is( "3" ) );
        Assert.assertThat( g.getMapLocation(), Matchers.is( "3q25.1" ) );
        Assert.assertThat( g.getDescription(), Matchers.is( "arylacetamide deacetylase" ) );
        Assert.assertThat( g.getType(), Matchers.is( "protein-coding" ) );
        Assert.assertThat( g.getSymbolNomenclatureAuthority(), Matchers.is( "AADAC" ) );
        Assert.assertThat( g.getFullNameNomenclatureAuthority(), Matchers.is( "arylacetamide deacetylase" ) );
        Assert.assertThat( g.getModificationDate(), Matchers.is( "20151015" ) );
    }

    private void assertIsGene2( Gene g ) {
        Assert.assertThat( g, Matchers.notNullValue() );
        Assert.assertThat( g.getGeneId(), Matchers.is( 32 ) );
        Assert.assertThat( g.getSymbol(), Matchers.is( "ACACB" ) );
        Assert.assertEquals( g.getSynonyms(), Sets.newHashSet( "ACC2", "ACCB", "HACC275" ) );
        Assert.assertEquals( g.getXrefs(), Sets.newHashSet( "MIM:601557", "HGNC:HGNC:85", "Ensembl:ENSG00000076555",
                "HPRD:07044", "Vega:OTTHUMG00000169250" ) );
        Assert.assertThat( g.getChromose(), Matchers.is( "12" ) );
        Assert.assertThat( g.getMapLocation(), Matchers.is( "12q24.11" ) );
        Assert.assertThat( g.getDescription(), Matchers.is( "acetyl-CoA carboxylase beta" ) );
        Assert.assertThat( g.getType(), Matchers.is( "protein-coding" ) );
        Assert.assertThat( g.getSymbolNomenclatureAuthority(), Matchers.is( "ACACB" ) );
        Assert.assertThat( g.getFullNameNomenclatureAuthority(), Matchers.is( "acetyl-CoA carboxylase beta" ) );
        Assert.assertThat( g.getModificationDate(), Matchers.is( "20151004" ) );
    }

}
