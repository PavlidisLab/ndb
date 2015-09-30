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

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Represents a Gene. Thread-safe.
 * 
 * @author mjacobson
 * @version $Id$
 */
public final class Gene {
    private final Integer id;
    private final String symbol;
    private final Integer size;
    private final Set<String> annovarSymbols;

    private Gene( GeneBuilder builder ) {
        this.id = builder.id;
        this.symbol = builder.symbol;
        this.size = builder.size;
        this.annovarSymbols = builder.annovarSymbols.build();
    }

    public Integer getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public Integer getSize() {
        return size;
    }

    public Set<String> getAnnovarSymbols() {
        return annovarSymbols;
    }

    @Override
    public String toString() {
        return "Gene [id=" + id + ", symbol=" + symbol + "]";
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
        Gene other = ( Gene ) obj;
        if ( id == null ) {
            if ( other.id != null ) return false;
        } else if ( !id.equals( other.id ) ) return false;
        return true;
    }

    public static class GeneBuilder {
        private final Integer id;
        private final String symbol;
        private final Integer size;
        private Builder<String> annovarSymbols = new ImmutableSet.Builder<String>();

        public GeneBuilder( Integer id, String symbol, Integer size ) {
            this.id = id;
            this.symbol = symbol;
            this.size = size;
        }

        public GeneBuilder annovarSymbol( String symbol ) {
            this.annovarSymbols.add( symbol );
            return this;
        }

        public Gene build() {
            return new Gene( this );
        }
    }

}
