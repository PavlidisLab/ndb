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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        Gene res = geneService.fetchGene( GENE1_ID );
        assertIsGene1( res );
    }

    @Test
    public void testFetchGene2() {
        Gene res = geneService.fetchGene( GENE2_SYMBOL );
        assertIsGene2( res );
    }

    @Test
    public void testListGenes() {
        List<Gene> res = geneService.listGenes();
        Assert.assertThat( res.size(), is( 19 ) );
    }

}
