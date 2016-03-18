/*
 * The ndb project
 * 
 * Copyright (category) 2016 University of British Columbia
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

package ubc.pavlab.ndb.utility;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class HeatmapModelTest {
    private static final Logger log = Logger.getLogger( HeatmapModelTest.class );
    private static final ArrayList<String> categories = Lists.newArrayList( "category1", "category2", "category3" );
    private HeatmapModel<String> heatmapModel;

    @Before
    public void setup() {
        heatmapModel = new HeatmapModel<String>( categories );
    }

    @After
    public void after() {
        heatmapModel = null;
    }

    @Test
    public void testAdd() {
        heatmapModel.add( 1, 1, 10.0 );
        Assert.assertThat( heatmapModel.getData().size(), Matchers.is( 1 ) );
        Assert.assertThat( heatmapModel.getData().get( 0 ), Matchers.is( new Number[] { 1, 1, 10.0 } ) );

        try {
            heatmapModel.add( 4, 1, 2 );
            Assert.fail( "X should be out of bounds" );
        } catch ( IndexOutOfBoundsException e ) {
            // Expected
        }

        try {
            heatmapModel.add( 1, 4, 2 );
            Assert.fail( "Y should be out of bounds" );
        } catch ( IndexOutOfBoundsException e ) {
            // Expected
        }

        heatmapModel.add( 2, 2, 20.0 );
        Assert.assertThat( heatmapModel.getData().size(), Matchers.is( 2 ) );
        Assert.assertThat( heatmapModel.getData().get( 1 ), Matchers.is( new Number[] { 2, 2, 20.0 } ) );
    }

    @Test
    public void testGetCategoriesJSON() {
        Assert.assertThat( heatmapModel.getCategoriesJSON(),
                Matchers.is( "[\"category1\",\"category2\",\"category3\"]" ) );
    }

    @Test
    public void testGetCategories() {
        Assert.assertEquals( heatmapModel.getCategories(), categories );
    }

    @Test
    public void testGetData() {
        heatmapModel.add( 1, 1, 10.0 );
        heatmapModel.add( 2, 2, 20.0 );
        heatmapModel.add( 0, 0, 30.0 );
        Assert.assertEquals( heatmapModel.getData().size(), 3 );
        Assert.assertThat( heatmapModel.getData().get( 0 ), Matchers.is( new Number[] { 1, 1, 10.0 } ) );
        Assert.assertThat( heatmapModel.getData().get( 1 ), Matchers.is( new Number[] { 2, 2, 20.0 } ) );
        Assert.assertThat( heatmapModel.getData().get( 2 ), Matchers.is( new Number[] { 0, 0, 30.0 } ) );
    }

    @Test
    public void testGetDataJSON() {
        heatmapModel.add( 1, 1, 10.0 );
        heatmapModel.add( 2, 2, 20.0 );
        heatmapModel.add( 0, 0, 30.0 );
        Assert.assertEquals( heatmapModel.getDataJSON(), "[[1,1,10.0],[2,2,20.0],[0,0,30.0]]" );
    }
}
