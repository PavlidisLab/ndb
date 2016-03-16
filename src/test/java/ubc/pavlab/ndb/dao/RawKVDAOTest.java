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
import ubc.pavlab.ndb.model.dto.RawKVDTO;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class RawKVDAOTest extends BaseTest {
    private static final Logger log = Logger.getLogger( RawKVDAOTest.class );
    private static RawKVDAO rawKVDAO;

    @BeforeClass
    public static void classSetup() {
        rawKVDAO = daoFactory.getRawKVDAO();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @Test
    public void find() {
        RawKVDTO dto = rawKVDAO.find( 14852 );
        assertIsRawKV1( dto );

        dto = rawKVDAO.find( 14853 );
        assertIsRawKV2( dto );
    }

    @Test
    public void findByPaperAndRawVariantId() {
        List<RawKVDTO> dtos = rawKVDAO.findByPaperAndRawVariantId( 7, 2836 );
        Assert.assertThat( dtos.size(), is( 16 ) );

        for ( RawKVDTO dto : dtos ) {
            if ( dto.getKey().equals( "vcfVariant" ) ) {
                Assert.assertThat( dto.getValue(), Matchers.is( "19:45912489:C:CAAG" ) );
            }
        }
    }

    @Test
    public void list() {

        List<RawKVDTO> dtos = rawKVDAO.list();
        Assert.assertThat( dtos.size(), is( 483 ) );
    }

    private void assertIsRawKV1( RawKVDTO dto ) {
        Assert.assertThat( dto, Matchers.notNullValue() );
        Assert.assertThat( dto.getId(), Matchers.is( 14852 ) );
        Assert.assertThat( dto.getPaperId(), Matchers.is( 7 ) );
        Assert.assertThat( dto.getRawId(), Matchers.is( 147 ) );
        Assert.assertThat( dto.getKey(), Matchers.is( "familyId" ) );
        Assert.assertThat( dto.getValue(), Matchers.is( "11074" ) );
    }

    private void assertIsRawKV2( RawKVDTO dto ) {
        Assert.assertThat( dto, Matchers.notNullValue() );
        Assert.assertThat( dto.getId(), Matchers.is( 14853 ) );
        Assert.assertThat( dto.getPaperId(), Matchers.is( 7 ) );
        Assert.assertThat( dto.getRawId(), Matchers.is( 147 ) );
        Assert.assertThat( dto.getKey(), Matchers.is( "location" ) );
        Assert.assertThat( dto.getValue(), Matchers.is( "17:43342073" ) );
    }

}
