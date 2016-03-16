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
import ubc.pavlab.ndb.model.dto.GeneDTO;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class GeneDAOTest extends BaseTest {

    private static final Logger log = Logger.getLogger( VariantDAOTest.class );
    private static GeneDAO geneDAO;

    @BeforeClass
    public static void classSetup() {
        geneDAO = daoFactory.getGeneDAO();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @Test
    public void testFind1() {
        GeneDTO dto = geneDAO.find( 32 );
        assertIsGene1( dto );
    }

    @Test
    public void testFind2() {
        GeneDTO dto = geneDAO.find( "ADSS" );
        assertIsGene2( dto );
    }

    @Test
    public void testList() {

        List<GeneDTO> dtos = geneDAO.list();
        Assert.assertThat( dtos.size(), is( 19 ) );
    }

    private void assertIsGene1( GeneDTO dto ) {
        Assert.assertThat( dto, Matchers.notNullValue() );
        Assert.assertThat( dto.getGeneId(), Matchers.is( 32 ) );
        Assert.assertThat( dto.getSymbol(), Matchers.is( "ACACB" ) );
        Assert.assertThat( dto.getSynonyms(), Matchers.is( "ACC2|ACCB|HACC275" ) );
        Assert.assertThat( dto.getXrefs(),
                Matchers.is( "MIM:601557|HGNC:HGNC:85|Ensembl:ENSG00000076555|HPRD:07044|Vega:OTTHUMG00000169250" ) );
        Assert.assertThat( dto.getChromose(), Matchers.is( "12" ) );
        Assert.assertThat( dto.getMapLocation(), Matchers.is( "12q24.11" ) );
        Assert.assertThat( dto.getDescription(), Matchers.is( "acetyl-CoA carboxylase beta" ) );
        Assert.assertThat( dto.getType(), Matchers.is( "protein-coding" ) );
        Assert.assertThat( dto.getSymbolNomenclatureAuthority(), Matchers.is( "ACACB" ) );
        Assert.assertThat( dto.getFullNameNomenclatureAuthority(), Matchers.is( "acetyl-CoA carboxylase beta" ) );
        Assert.assertThat( dto.getModificationDate(), Matchers.is( "20151004" ) );
    }

    private void assertIsGene2( GeneDTO dto ) {
        Assert.assertThat( dto, Matchers.notNullValue() );
        Assert.assertThat( dto.getGeneId(), Matchers.is( 159 ) );
        Assert.assertThat( dto.getSymbol(), Matchers.is( "ADSS" ) );
        Assert.assertThat( dto.getSynonyms(), Matchers.is( "ADEH|ADSS 2" ) );
        Assert.assertThat( dto.getXrefs(),
                Matchers.is( "MIM:103060|HGNC:HGNC:292|Ensembl:ENSG00000035687|HPRD:00050|Vega:OTTHUMG00000040102" ) );
        Assert.assertThat( dto.getChromose(), Matchers.is( "1" ) );
        Assert.assertThat( dto.getMapLocation(), Matchers.is( "1q44" ) );
        Assert.assertThat( dto.getDescription(), Matchers.is( "adenylosuccinate synthase" ) );
        Assert.assertThat( dto.getType(), Matchers.is( "protein-coding" ) );
        Assert.assertThat( dto.getSymbolNomenclatureAuthority(), Matchers.is( "ADSS" ) );
        Assert.assertThat( dto.getFullNameNomenclatureAuthority(), Matchers.is( "adenylosuccinate synthase" ) );
        Assert.assertThat( dto.getModificationDate(), Matchers.is( "20151004" ) );
    }
}
