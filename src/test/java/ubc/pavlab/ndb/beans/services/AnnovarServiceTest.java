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
import org.junit.After;
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
        Annovar res = annovarService.fetchById( ANNOVAR1_ID );
        assertIsAnnovar1( res );
    }

    @Test
    public void testFetchByVariantId() {
        Annovar res = annovarService.fetchById( ANNOVAR1_VARIANT_ID );
        assertIsAnnovar1( res );
    }

}
