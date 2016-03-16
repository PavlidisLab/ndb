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
 * distributed under the License Matchers.is distributed on an "AS Matchers.is" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ubc.pavlab.ndb.dao;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ubc.pavlab.ndb.BaseTest;
import ubc.pavlab.ndb.model.dto.VariantDTO;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class VariantDAOTest extends BaseTest {

    private static final Logger log = Logger.getLogger( VariantDAOTest.class );
    private static VariantDAO variantDAO;

    @BeforeClass
    public static void classSetup() {
        variantDAO = daoFactory.getVariantDAO();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    //    public void find( String sql, Object... values ) {
    //    }
    //
    //    public void findAll( String sql, Object... values ) {
    //    }
    //
    //    public void map( ResultSet resultSet ) {
    //    }

    // -------------------------
    @Test
    public void testfind() {
        VariantDTO dto = variantDAO.find( 120 );
        assertIsVariant1( dto );
    }

    @Test
    public void testfind2() {
        List<VariantDTO> dtos = variantDAO.find( Lists.newArrayList( 120, 129 ) );
        Assert.assertThat( dtos.size(), Matchers.is( 2 ) );
        VariantDTO dto = dtos.get( 0 );

        assertIsVariant1( dto );

        dto = dtos.get( 1 );

        assertIsVariant2( dto );
    }

    @Test
    public void testFindByPaperId() {
        List<VariantDTO> dtos = variantDAO.findByPaperId( 7 );

        Assert.assertThat( dtos.size(), Matchers.is( 8 ) );

        Set<Integer> ids = Sets.newHashSet( 1748, 3444, 1688, 1666, 1846, 3481, 3536, 2798 );

        for ( VariantDTO dto : dtos ) {
            Assert.assertTrue( ids.contains( dto.getId() ) );
            ids.remove( dto.getId() );
        }

    }

    @Test
    public void testFindByEventId() {
        List<VariantDTO> dtos = variantDAO.findByEventId( 3558 );
        Assert.assertThat( dtos.size(), Matchers.is( 1 ) );
        VariantDTO dto = dtos.get( 0 );

        assertIsVariant2( dto );
    }

    @Test
    public void testFindBySubjectId() {
        List<VariantDTO> dtos = variantDAO.findBySubjectId( 1607 );
        Assert.assertThat( dtos.size(), Matchers.is( 1 ) );
        VariantDTO dto = dtos.get( 0 );

        assertIsVariant1( dto );
    }

    @Test
    public void testFindByPosition() {
        List<VariantDTO> dtos = variantDAO.findByPosition( "3", 14703152, 151538186 );
        Assert.assertThat( dtos.size(), Matchers.is( 2 ) );

        for ( VariantDTO dto : dtos ) {
            if ( dto.getId() == 120 ) {
                assertIsVariant1( dto );
            } else if ( dto.getId() == 273 ) {
                assertIsVariant3( dto );
            } else {
                Assert.fail( "Incorrect Variant" );
            }
        }

        dtos = variantDAO.findByPosition( "2", 14703152, 151538186 );
        Assert.assertThat( dtos.size(), Matchers.is( 0 ) );

        dtos = variantDAO.findByPosition( "12", 109577549, 109577549 );
        Assert.assertThat( dtos.size(), Matchers.is( 1 ) );

        VariantDTO dto = dtos.get( 0 );

        assertIsVariant2( dto );
    }

    @Test
    public void testFindByPaperOverlap() {
        // overlap 7, 18 gives 832, 3481, 3536, 141
        List<VariantDTO> dtos = variantDAO.findByPaperOverlap( 7, 18 );

        Assert.assertThat( dtos.size(), Matchers.is( 4 ) );

        Set<Integer> ids = Sets.newHashSet( 832, 3481, 3536, 141 );

        for ( VariantDTO dto : dtos ) {
            Assert.assertTrue( ids.contains( dto.getId() ) );
            ids.remove( dto.getId() );
        }

        dtos = variantDAO.findByPaperOverlap( 7, 16 );

        Assert.assertThat( dtos.size(), Matchers.is( 2 ) );

        ids = Sets.newHashSet( 2798, 3722 );

        for ( VariantDTO dto : dtos ) {
            Assert.assertTrue( ids.contains( dto.getId() ) );
            ids.remove( dto.getId() );
        }
    }

    @Test
    public void testList() {
        List<VariantDTO> dtos = variantDAO.list();
        Assert.assertThat( dtos.size(), Matchers.is( 22 ) );
    }

    @Test
    public void testFindGeneIdsForVariantId() {
        List<Integer> res = variantDAO.findGeneIdsForVariantId( 120 );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        Assert.assertThat( res.get( 0 ), Matchers.is( 13 ) );

        res = variantDAO.findGeneIdsForVariantId( 605 );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        Assert.assertThat( res.get( 0 ), Matchers.is( 65983 ) );
    }

    @Test
    public void testFindVariantIdsForGeneId() {
        List<Integer> res = variantDAO.findVariantIdsForGeneId( 13 );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        Assert.assertThat( res.get( 0 ), Matchers.is( 120 ) );

        res = variantDAO.findVariantIdsForGeneId( 65983 );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        Assert.assertThat( res.get( 0 ), Matchers.is( 605 ) );
    }

    private void assertIsVariant1( VariantDTO dto ) {
        Assert.assertThat( dto, Matchers.notNullValue() );
        Assert.assertThat( dto.getId(), Matchers.is( 120 ) );
        Assert.assertThat( dto.getPaperId(), Matchers.is( 18 ) );
        Assert.assertThat( dto.getRawVariantId(), Matchers.is( 736 ) );
        Assert.assertThat( dto.getEventId(), Matchers.is( 741 ) );
        Assert.assertThat( dto.getSubjectId(), Matchers.is( 1607 ) );
        Assert.assertThat( dto.getSampleId(), Matchers.is( "09C84345" ) );
        Assert.assertThat( dto.getChromosome(), Matchers.is( "3" ) );
        Assert.assertThat( dto.getStartHg19(), Matchers.is( 151538186 ) );
        Assert.assertThat( dto.getStopHg19(), Matchers.is( 151538186 ) );
        Assert.assertThat( dto.getRef(), Matchers.is( "A" ) );
        Assert.assertThat( dto.getAlt(), Matchers.is( "C" ) );
        Assert.assertThat( dto.getGene(), Matchers.is( "AADAC" ) );
        Assert.assertThat( dto.getCategory(), Matchers.is( "nonsynonymous SNV" ) );
        Assert.assertNull( dto.getGeneDetail() );
        Assert.assertThat( dto.getFunc(), Matchers.is( "exonic" ) );
        Assert.assertThat( dto.getAaChange(), Matchers.is( "AADAC:NM_001086:exon3:c.A377C:p.D126A," ) );
        Assert.assertThat( dto.getCytoband(), Matchers.is( "3q25.1" ) );
        Assert.assertThat( dto.getDenovo(), Matchers.is( "yes" ) );
        Assert.assertThat( dto.getLoF(), Matchers.is( "unknown" ) );
    }

    private void assertIsVariant2( VariantDTO dto ) {
        Assert.assertThat( dto, Matchers.notNullValue() );
        Assert.assertThat( dto.getId(), Matchers.is( 129 ) );
        Assert.assertThat( dto.getPaperId(), Matchers.is( 18 ) );
        Assert.assertThat( dto.getRawVariantId(), Matchers.is( 2051 ) );
        Assert.assertThat( dto.getEventId(), Matchers.is( 3558 ) );
        Assert.assertThat( dto.getSubjectId(), Matchers.is( 122 ) );
        Assert.assertThat( dto.getSampleId(), Matchers.is( "13585" ) );
        Assert.assertThat( dto.getChromosome(), Matchers.is( "12" ) );
        Assert.assertThat( dto.getStartHg19(), Matchers.is( 109577549 ) );
        Assert.assertThat( dto.getStopHg19(), Matchers.is( 109577549 ) );
        Assert.assertThat( dto.getRef(), Matchers.is( "A" ) );
        Assert.assertThat( dto.getAlt(), Matchers.is( "AG" ) );
        Assert.assertThat( dto.getGene(), Matchers.is( "ACACB" ) );
        Assert.assertThat( dto.getCategory(), Matchers.is( "frameshift insertion" ) );
        Assert.assertNull( dto.getGeneDetail() );
        Assert.assertThat( dto.getFunc(), Matchers.is( "exonic" ) );
        Assert.assertThat( dto.getAaChange(), Matchers.is( "ACACB:NM_001093:exon1:c.340dupG:p.P113fs," ) );
        Assert.assertThat( dto.getCytoband(), Matchers.is( "12q24.11" ) );
        Assert.assertThat( dto.getDenovo(), Matchers.is( "yes" ) );
        Assert.assertNull( dto.getLoF() );
    }

    private void assertIsVariant3( VariantDTO dto ) {
        Assert.assertThat( dto, Matchers.notNullValue() );
        Assert.assertThat( dto.getId(), Matchers.is( 273 ) );
        Assert.assertThat( dto.getPaperId(), Matchers.is( 18 ) );
        Assert.assertThat( dto.getRawVariantId(), Matchers.is( 668 ) );
        Assert.assertThat( dto.getEventId(), Matchers.is( 862 ) );
        Assert.assertThat( dto.getSubjectId(), Matchers.is( 2174 ) );
        Assert.assertThat( dto.getSampleId(), Matchers.is( "NDAR_INVFM678KRA_wes1" ) );
        Assert.assertThat( dto.getChromosome(), Matchers.is( "3" ) );
        Assert.assertThat( dto.getStartHg19(), Matchers.is( 14703152 ) );
        Assert.assertThat( dto.getStopHg19(), Matchers.is( 14703152 ) );
        Assert.assertThat( dto.getRef(), Matchers.is( "C" ) );
        Assert.assertThat( dto.getAlt(), Matchers.is( "T" ) );
        Assert.assertThat( dto.getGene(), Matchers.is( "CCDC174" ) );
        Assert.assertThat( dto.getCategory(), Matchers.is( "synonymous SNV" ) );
        Assert.assertNull( dto.getGeneDetail() );
        Assert.assertThat( dto.getFunc(), Matchers.is( "exonic" ) );
        Assert.assertThat( dto.getAaChange(), Matchers.is( "CCDC174:NM_016474:exon5:c.C423T:p.D141D," ) );
        Assert.assertThat( dto.getCytoband(), Matchers.is( "3p25.1" ) );
        Assert.assertThat( dto.getDenovo(), Matchers.is( "yes" ) );
        Assert.assertThat( dto.getLoF(), Matchers.is( "unknown" ) );
    }

}
