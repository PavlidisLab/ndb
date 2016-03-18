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

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ubc.pavlab.ndb.BaseTest;
import ubc.pavlab.ndb.model.Variant;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class VariantServiceTest extends BaseTest {
    private static final Logger log = Logger.getLogger( VariantServiceTest.class );
    private static VariantService variantService;

    @BeforeClass
    public static void classSetup() {
        variantService = getMockVariantService();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @Test
    public void testFetchById1() {
        Variant res = variantService.fetchById( VARIANT1_ID );
        assertIsVariant1( res );

        res = variantService.fetchById( VARIANT2_ID );
        assertIsVariant2( res );

        res = variantService.fetchById( VARIANT3_ID );
        assertIsVariant3( res );
    }

    @Test
    public void testFetchById2() {
        List<Variant> res = variantService.fetchById( Lists.newArrayList( VARIANT1_ID, VARIANT2_ID, VARIANT3_ID ) );
        Assert.assertThat( res.size(), Matchers.is( 3 ) );
        assertIsVariant1( res.get( 0 ) );

        assertIsVariant2( res.get( 1 ) );

        assertIsVariant3( res.get( 2 ) );
    }

    @Test
    public void testFetchByPaperId() {
        List<Variant> res = variantService.fetchByPaperId( 7 );
        Assert.assertThat( res.size(), Matchers.is( 8 ) );

        Set<Integer> ids = Sets.newHashSet( 1748, 3444, 1688, 1666, 1846, 3481, 3536, 2798 );

        for ( Variant v : res ) {
            Assert.assertTrue( ids.contains( v.getId() ) );
            ids.remove( v.getId() );
        }
    }

    @Test
    public void testFetchByPaperOverlap() {

        // overlap 7, 18 gives 832, 3481, 3536, 141
        List<Variant> res = variantService.fetchByPaperOverlap( 7, 18 );

        Assert.assertThat( res.size(), Matchers.is( 4 ) );

        Set<Integer> ids = Sets.newHashSet( 832, 3481, 3536, 141 );

        for ( Variant v : res ) {
            Assert.assertTrue( ids.contains( v.getId() ) );
            ids.remove( v.getId() );
        }

        res = variantService.fetchByPaperOverlap( 7, 16 );

        Assert.assertThat( res.size(), Matchers.is( 2 ) );

        ids = Sets.newHashSet( 2798, 3722 );

        for ( Variant v : res ) {
            Assert.assertTrue( ids.contains( v.getId() ) );
            ids.remove( v.getId() );
        }
    }

    @Test
    public void testFetchByEventId() {
        List<Variant> res = variantService.fetchByEventId( VARIANT1_EVENT_ID );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        assertIsVariant1( res.get( 0 ) );

        res = variantService.fetchByEventId( VARIANT2_EVENT_ID );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        assertIsVariant2( res.get( 0 ) );

        res = variantService.fetchByEventId( VARIANT3_EVENT_ID );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        assertIsVariant3( res.get( 0 ) );

        res = variantService.fetchByEventId( 82 );
        Assert.assertThat( res.size(), Matchers.is( 2 ) );
    }

    @Test
    public void testFetchBySubjectId() {
        List<Variant> res = variantService.fetchBySubjectId( VARIANT1_SUBJECT_ID );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        assertIsVariant1( res.get( 0 ) );

        res = variantService.fetchBySubjectId( VARIANT2_SUBJECT_ID );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        assertIsVariant2( res.get( 0 ) );

        res = variantService.fetchBySubjectId( VARIANT3_SUBJECT_ID );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        assertIsVariant3( res.get( 0 ) );

        res = variantService.fetchBySubjectId( 624 );
        Assert.assertThat( res.size(), Matchers.is( 2 ) );
    }

    @Test
    public void testFetchByPosition() {
        List<Variant> res = variantService.fetchByPosition( "3", 14703152, 151538186 );
        Assert.assertThat( res.size(), Matchers.is( 2 ) );

        for ( Variant v : res ) {
            if ( v.getId() == VARIANT1_ID ) {
                assertIsVariant1( v );
            } else if ( v.getId() == VARIANT3_ID ) {
                assertIsVariant3( v );
            } else {
                Assert.fail( "Incorrect Variant" );
            }
        }

        res = variantService.fetchByPosition( "2", 14703152, 151538186 );
        Assert.assertThat( res.size(), Matchers.is( 0 ) );

        res = variantService.fetchByPosition( "12", 109577549, 109577549 );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );

        assertIsVariant2( res.get( 0 ) );
    }

    @Test
    public void testFetchByGeneId() {
        List<Variant> res = variantService.fetchByGeneId( 13 );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        assertIsVariant1( res.get( 0 ) );

        res = variantService.fetchByGeneId( 65983 );
        Assert.assertThat( res.size(), Matchers.is( 1 ) );
        Assert.assertThat( res.get( 0 ), Matchers.notNullValue() );
        Assert.assertThat( res.get( 0 ).getId(), Matchers.is( 605 ) );
    }

    // -----------------------

    @Test
    @Ignore
    public void testProxyFetching() {
        // TODO Not sure how to do this
    }
}
