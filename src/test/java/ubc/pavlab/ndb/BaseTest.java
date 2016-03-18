package ubc.pavlab.ndb;

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import ubc.pavlab.ndb.beans.ApplicationProperties;
import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.beans.services.AnnovarService;
import ubc.pavlab.ndb.beans.services.CacheService;
import ubc.pavlab.ndb.beans.services.GeneService;
import ubc.pavlab.ndb.beans.services.PaperService;
import ubc.pavlab.ndb.beans.services.RawKVService;
import ubc.pavlab.ndb.beans.services.StatsService;
import ubc.pavlab.ndb.beans.services.VariantService;
import ubc.pavlab.ndb.dao.DAOFactory;
import ubc.pavlab.ndb.exceptions.ConfigurationException;
import ubc.pavlab.ndb.model.Annovar;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.model.Variant;
import ubc.pavlab.ndb.model.enums.Category;

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

    private static CacheService cacheService = null;
    private static VariantService variantService = null;
    private static StatsService statsService = null;

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

    protected static CacheService getMockCacheService() {
        if ( cacheService == null ) {
            cacheService = new CacheService();
            cacheService.setDaoFactoryBean( daoFactoryBean );

            // Inject services
            cacheService.setGeneService( getMockGeneService() );
            cacheService.setPaperService( getMockPaperService() );

            cacheService.init();
        }

        return cacheService;
    }

    protected static VariantService getMockVariantService() {
        if ( variantService == null ) {
            variantService = new VariantService();
            variantService.setDaoFactoryBean( daoFactoryBean );

            // Inject services
            variantService.setAnnovarService( getMockAnnovarService() );
            variantService.setRawKVService( getMockRawKVService() );
            variantService.setCacheService( getMockCacheService() );

            variantService.init();
        }

        return variantService;
    }

    protected static StatsService getMockStatsService() {
        if ( statsService == null ) {
            statsService = new StatsService();
            statsService.setDaoFactoryBean( daoFactoryBean );

            // Inject services
            statsService.setCacheService( getMockCacheService() );

            statsService.init();
        }

        return statsService;
    }

    // Utility functions

    protected static final int VARIANT1_ID = 120;
    protected static final int VARIANT1_EVENT_ID = 741;
    protected static final int VARIANT1_SUBJECT_ID = 1607;

    protected void assertIsVariant1( Variant v ) {
        Assert.assertThat( v, Matchers.notNullValue() );
        Assert.assertThat( v.getId(), Matchers.is( 120 ) );

        Assert.assertThat( v.getPaper(), Matchers.notNullValue() );
        Assert.assertThat( v.getPaper().getId(), Matchers.is( 18 ) );

        Assert.assertThat( v.getRawVariantId(), Matchers.is( 736 ) );
        Assert.assertThat( v.getEventId(), Matchers.is( 741 ) );
        Assert.assertThat( v.getSubjectId(), Matchers.is( 1607 ) );
        Assert.assertThat( v.getSampleId(), Matchers.is( "09C84345" ) );
        Assert.assertThat( v.getChromosome(), Matchers.is( "3" ) );
        Assert.assertThat( v.getStartHg19(), Matchers.is( 151538186 ) );
        Assert.assertThat( v.getStopHg19(), Matchers.is( 151538186 ) );
        Assert.assertThat( v.getRef(), Matchers.is( "A" ) );
        Assert.assertThat( v.getAlt(), Matchers.is( "C" ) );

        Assert.assertThat( v.getGenes(), Matchers.notNullValue() );
        Assert.assertThat( v.getGenes().size(), Matchers.is( 1 ) );
        assertIsGene1( v.getGenes().get( 0 ) );

        Assert.assertThat( v.getCategory(), Matchers.is( Category.nonsynonymousSNV ) );

        Assert.assertNull( v.getGeneDetail() );
        Assert.assertThat( v.getFunc(), Matchers.is( "exonic" ) );

        Assert.assertThat( v.getAaChanges(), Matchers.notNullValue() );
        Assert.assertThat( v.getAaChanges().size(), Matchers.is( 1 ) );
        Assert.assertThat( v.getAaChanges().get( 0 ), Matchers.is( "AADAC:NM_001086:exon3:c.A377C:p.D126A" ) );

        Assert.assertThat( v.getCytoband(), Matchers.is( "3q25.1" ) );
        Assert.assertThat( v.getDenovo(), Matchers.is( "yes" ) );
        Assert.assertThat( v.getLoF(), Matchers.is( "unknown" ) );

        Assert.assertThat( v.getRawKV(), Matchers.notNullValue() );
        Assert.assertThat( v.getRawKV().size(), Matchers.is( 26 ) );

        Assert.assertThat( v.getAnnovar(), Matchers.notNullValue() );
        Assert.assertThat( v.getAnnovar().getVariantId(), Matchers.is( 120 ) );

    }

    protected static final int VARIANT2_ID = 129;
    protected static final int VARIANT2_EVENT_ID = 3558;
    protected static final int VARIANT2_SUBJECT_ID = 122;

    protected void assertIsVariant2( Variant v ) {
        Assert.assertThat( v, Matchers.notNullValue() );
        Assert.assertThat( v.getId(), Matchers.is( 129 ) );

        Assert.assertThat( v.getPaper(), Matchers.notNullValue() );
        Assert.assertThat( v.getPaper().getId(), Matchers.is( 18 ) );

        Assert.assertThat( v.getRawVariantId(), Matchers.is( 2051 ) );
        Assert.assertThat( v.getEventId(), Matchers.is( 3558 ) );
        Assert.assertThat( v.getSubjectId(), Matchers.is( 122 ) );
        Assert.assertThat( v.getSampleId(), Matchers.is( "13585" ) );
        Assert.assertThat( v.getChromosome(), Matchers.is( "12" ) );
        Assert.assertThat( v.getStartHg19(), Matchers.is( 109577549 ) );
        Assert.assertThat( v.getStopHg19(), Matchers.is( 109577549 ) );
        Assert.assertThat( v.getRef(), Matchers.is( "A" ) );
        Assert.assertThat( v.getAlt(), Matchers.is( "AG" ) );

        Assert.assertThat( v.getGenes(), Matchers.notNullValue() );
        Assert.assertThat( v.getGenes().size(), Matchers.is( 1 ) );
        assertIsGene2( v.getGenes().get( 0 ) );

        Assert.assertThat( v.getCategory(), Matchers.is( Category.frameshiftInsertion ) );

        Assert.assertNull( v.getGeneDetail() );
        Assert.assertThat( v.getFunc(), Matchers.is( "exonic" ) );

        Assert.assertThat( v.getAaChanges(), Matchers.notNullValue() );
        Assert.assertThat( v.getAaChanges().size(), Matchers.is( 1 ) );
        Assert.assertThat( v.getAaChanges().get( 0 ), Matchers.is( "ACACB:NM_001093:exon1:c.340dupG:p.P113fs" ) );

        Assert.assertThat( v.getCytoband(), Matchers.is( "12q24.11" ) );
        Assert.assertThat( v.getDenovo(), Matchers.is( "yes" ) );
        Assert.assertNull( v.getLoF() );

        Assert.assertThat( v.getRawKV(), Matchers.notNullValue() );
        Assert.assertThat( v.getRawKV().size(), Matchers.is( 26 ) );

        Assert.assertThat( v.getAnnovar(), Matchers.notNullValue() );
        Assert.assertThat( v.getAnnovar().getVariantId(), Matchers.is( 129 ) );
    }

    protected static final int VARIANT3_ID = 273;
    protected static final int VARIANT3_EVENT_ID = 862;
    protected static final int VARIANT3_SUBJECT_ID = 2174;

    protected void assertIsVariant3( Variant v ) {
        Assert.assertThat( v, Matchers.notNullValue() );
        Assert.assertThat( v.getId(), Matchers.is( 273 ) );

        Assert.assertThat( v.getPaper(), Matchers.notNullValue() );
        Assert.assertThat( v.getPaper().getId(), Matchers.is( 18 ) );

        Assert.assertThat( v.getRawVariantId(), Matchers.is( 668 ) );
        Assert.assertThat( v.getEventId(), Matchers.is( 862 ) );
        Assert.assertThat( v.getSubjectId(), Matchers.is( 2174 ) );
        Assert.assertThat( v.getSampleId(), Matchers.is( "NDAR_INVFM678KRA_wes1" ) );
        Assert.assertThat( v.getChromosome(), Matchers.is( "3" ) );
        Assert.assertThat( v.getStartHg19(), Matchers.is( 14703152 ) );
        Assert.assertThat( v.getStopHg19(), Matchers.is( 14703152 ) );
        Assert.assertThat( v.getRef(), Matchers.is( "C" ) );
        Assert.assertThat( v.getAlt(), Matchers.is( "T" ) );

        Assert.assertThat( v.getGenes(), Matchers.notNullValue() );
        Assert.assertThat( v.getGenes().size(), Matchers.is( 1 ) );
        Assert.assertThat( v.getGenes().get( 0 ).getSymbol(), Matchers.is( "CCDC174" ) );

        Assert.assertThat( v.getCategory(), Matchers.is( Category.synonymousSNV ) );

        Assert.assertNull( v.getGeneDetail() );
        Assert.assertThat( v.getFunc(), Matchers.is( "exonic" ) );

        Assert.assertThat( v.getAaChanges(), Matchers.notNullValue() );
        Assert.assertThat( v.getAaChanges().size(), Matchers.is( 1 ) );
        Assert.assertThat( v.getAaChanges().get( 0 ), Matchers.is( "CCDC174:NM_016474:exon5:c.C423T:p.D141D" ) );

        Assert.assertThat( v.getCytoband(), Matchers.is( "3p25.1" ) );
        Assert.assertThat( v.getDenovo(), Matchers.is( "yes" ) );
        Assert.assertThat( v.getLoF(), Matchers.is( "unknown" ) );

        Assert.assertThat( v.getRawKV(), Matchers.notNullValue() );
        Assert.assertThat( v.getRawKV().size(), Matchers.is( 26 ) );

        Assert.assertThat( v.getAnnovar(), Matchers.notNullValue() );
        Assert.assertThat( v.getAnnovar().getVariantId(), Matchers.is( 273 ) );
    }

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
    protected static final String PAPER1_AUTHOR = "Iossifov";

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
    protected static final String PAPER2_AUTHOR = "O'Roak";

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
