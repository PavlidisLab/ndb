/*
 * The ndb project
 * 
 * Copyright (c) 2015 University of British Columbia
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

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

/**
 * TODO Document Me
 * 
 * @author mbelmadani
 * @author mjacobson
 * @version $Id$
 */

public class HeatmapModel<M> {

    private final List<M> categories;
    private final List<Number[]> data;

    public HeatmapModel( List<M> categories ) {
        this.categories = categories;
        this.data = Lists.newArrayList();
    }

    public <T extends Number> boolean add( int x, int y, T value ) {
        if ( x >= categories.size() ) {
            throw new IndexOutOfBoundsException( "x: " + String.valueOf( x ) + " >= total number of categories" );
        }

        if ( y >= categories.size() ) {
            throw new IndexOutOfBoundsException( "y: " + String.valueOf( y ) + " >= total number of categories" );
        }

        return data.add( new Number[] { x, y, value } );
    }

    public String getCategoriesJSON() {
        Gson gson = new Gson();
        return gson.toJson( categories );
    }

    public List<M> getCategories() {
        return this.categories;
    }

    public String getDataJSON() {
        Gson gson = new Gson();
        return gson.toJson( data );
    }

    public List<Number[]> getData() {
        return data;
    }
}
