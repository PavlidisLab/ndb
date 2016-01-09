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

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Document Me
 * 
 * @author mbelmadani
 * @version $Id$
 */

public class HeatmapModel {

    private List<String> categories;
    private List<List<Integer>> data;

    public HeatmapModel() {
        categories = new ArrayList<String>();
        data = new ArrayList<>();
    }

    public void insertPoints( List<Correlation> corrs ) {
        for ( Correlation c : corrs ) {
            this.insertPoint( c );
        }
    }

    public void insertPoint( Correlation c ) {
        if ( !categories.contains( c.x ) ) {
            categories.add( c.x );
        }
        if ( !categories.contains( c.y ) ) {
            categories.add( c.y );
        }

        ArrayList<Integer> datum = new ArrayList<Integer>( 3 );

        datum.add( categories.indexOf( c.x ) );
        datum.add( categories.indexOf( c.y ) );
        datum.add( c.getValue() );

        data.add( datum );
    }

    public List<String> getTextCategories() {
        /*
         * Special getter to get the string enclosed names
         */

        List<String> textCategories = new ArrayList<String>();
        for ( String s : this.categories ) {
            textCategories.add( '"' + s + '"' );
        }

        return textCategories;
    }

    public List<String> getCategories() {
        return this.categories;
    }

    public List<List<Integer>> getData() {
        return data;
    }
}
