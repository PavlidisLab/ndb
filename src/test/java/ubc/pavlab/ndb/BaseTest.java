package ubc.pavlab.ndb;

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import ubc.pavlab.ndb.beans.ApplicationProperties;
import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.beans.services.AnnovarService;
import ubc.pavlab.ndb.beans.services.GeneService;
import ubc.pavlab.ndb.beans.services.PaperService;
import ubc.pavlab.ndb.beans.services.RawKVService;
import ubc.pavlab.ndb.dao.DAOFactory;
import ubc.pavlab.ndb.exceptions.ConfigurationException;
import ubc.pavlab.ndb.model.Annovar;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Paper;

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

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class BaseTest {

    private static final Logger log = Logger.getLogger( BaseTest.class );

    private static final String PROPERTY_TESTDB = "ndb.testdb";

    protected static final ApplicationProperties applicationProperties;
    protected static final DAOFactory daoFactory;
    protected static final DAOFactoryBean daoFactoryBean;

    private static AnnovarService annovarService = null;
    private static GeneService geneService = null;
    private static PaperService paperService = null;
    private static RawKVService rawKVService = null;

    static {

        applicationProperties = new ApplicationProperties();
        applicationProperties.init();
        applicationProperties.getBasePropertiesFile().mute();

        String testdbKey = applicationProperties.getProperty( PROPERTY_TESTDB );
        if ( testdbKey == null ) {
            throw new ConfigurationException( "Required Property '" + PROPERTY_TESTDB + "'"
                    + " is missing in properties file '" + applicationProperties.getPropertiesFile() + "'." );
        }

        daoFactory = DAOFactory.getInstance( testdbKey );
        log.info( "TestDAOFactory successfully obtained: " + daoFactory );

        daoFactoryBean = Mockito.mock( DAOFactoryBean.class );
        Mockito.when( daoFactoryBean.getDAOFactory() ).thenReturn( daoFactory );

        // Application Scoped Services

    }

    public BaseTest() {

    }

    protected static AnnovarService getMockAnnovarService() {
        if ( annovarService == null ) {
            annovarService = new AnnovarService();
            annovarService.setDaoFactoryBean( daoFactoryBean );
            annovarService.init();
        }

        return annovarService;
    }

    protected static GeneService getMockGeneService() {
        if ( geneService == null ) {
            geneService = new GeneService();
            geneService.setDaoFactoryBean( daoFactoryBean );
            geneService.init();
        }

        return geneService;
    }

    protected static PaperService getMockPaperService() {
        if ( paperService == null ) {
            paperService = new PaperService();
            paperService.setDaoFactoryBean( daoFactoryBean );
            paperService.init();
        }

        return paperService;
    }

    protected static RawKVService getMockRawKVService() {
        if ( rawKVService == null ) {
            rawKVService = new RawKVService();
            rawKVService.setDaoFactoryBean( daoFactoryBean );
            rawKVService.init();
        }

        return rawKVService;
    }

    // Utility functions

    protected static final int GENE1_ID = 13;
    protected static final String GENE1_SYMBOL = "AADAC";

    protected void assertIsGene1( Gene g ) {
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

    protected static final int GENE2_ID = 32;
    protected static final String GENE2_SYMBOL = "ACACB";

    protected void assertIsGene2( Gene g ) {
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

    protected static final int ANNOVAR1_ID = 120;
    protected static final int ANNOVAR1_VARIANT_ID = 120;

    protected void assertIsAnnovar1( Annovar ent ) {
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

    protected static final int PAPER1_ID = 7;
    protected static final String PAPER1_KEY = "Iossifov2";

    protected void assertIsPaper1( Paper e ) {
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

    protected static final int PAPER2_ID = 16;
    protected static final String PAPER2_KEY = "O'Roak";

    protected void assertIsPaper2( Paper e ) {
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
