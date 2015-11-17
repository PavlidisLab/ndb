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

package ubc.pavlab.ndb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents a Event-variant grouping
 * 
 * @author mbelmadani
 * @version $Id$
 */
public class EventListFactory {
    private Multimap<String, Variant> dict;

    public EventListFactory( List<Variant> mutations ) {

        this.dict = HashMultimap.create();
        for ( Variant v : mutations ) {
            this.dict.put( Integer.toString( v.getEventId() ), v );
        }
    }

    public List<Event> getEventList() {
        ArrayList<String> uniqueKeys = new ArrayList<String>( this.dict.keySet() );
        Collections.sort( uniqueKeys );

        List<Event> events = new ArrayList<Event>();

        for ( String s : uniqueKeys ) {
            List<Variant> variants = new ArrayList<Variant>( this.dict.get( s ) );
            Event event = new Event( variants );
            events.add( event );

        }
        return events;
    }
}
