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

import java.util.List;
import java.util.Map.Entry;

/**
 * TODO Document Me
 * 
 * @author mbelmadani
 * @version $Id$
 */
public class RawKeyValue {
    private final Integer id;
    private final Paper paper;
    private List<Entry<String, String>> entries;

    public RawKeyValue( int id, Paper paper, List<Entry<String, String>> entries ) {
        this.id = id;
        this.paper = paper;
        this.entries = entries;
    }

    public Integer getId() {
        return id;
    }

    public Paper getPaper() {
        return paper;
    }

    @Override
    public String toString() {
        return "Raw Mutation [id=" + id + ", paper=" + paper + ", raw=" + entries.toString() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        RawKeyValue other = ( RawKeyValue ) obj;
        if ( id == null ) {
            if ( other.id != null ) return false;
        } else if ( !id.equals( other.id ) ) return false;
        return true;
    }

    public List<Entry<String, String>> getEntries() {
        return entries;
    }

    public void setEntries( List<Entry<String, String>> entries ) {
        this.entries = entries;
    }

}