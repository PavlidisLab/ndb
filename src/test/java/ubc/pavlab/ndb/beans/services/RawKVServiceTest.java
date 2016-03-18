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

import ubc.pavlab.ndb.BaseTest;
import ubc.pavlab.ndb.model.RawKV;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class RawKVServiceTest extends BaseTest {
    private static final Logger log = Logger.getLogger( RawKVServiceTest.class );
    private static RawKVService rawKVService;

    @BeforeClass
    public static void classSetup() {
        rawKVService = getMockRawKVService();
    }

    @Before
    public void setup() {

    }

    @After
    public void after() {

    }

    @Test
    public void testFetchByPaperAndRawVariantId() {
        List<RawKV> res = rawKVService.fetchByPaperAndRawVariantId( 7, 2836 );
        Assert.assertThat( res.size(), is( 16 ) );

        for ( RawKV kv : res ) {
            if ( kv.getKey().equals( "vcfVariant" ) ) {
                Assert.assertThat( kv.getValue(), Matchers.is( "19:45912489:C:CAAG" ) );
            }
        }
    }
}
