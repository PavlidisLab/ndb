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

import ubc.pavlab.ndb.model.dto.GeneDTO;

/**
 * Represents a Gene. Thread-safe.
 * 
 * @author mjacobson
 * @version $Id$
 */
public final class Gene implements Comparable<Gene> {
    private final Integer geneId;
    private final String symbol;
    private final Set<String> synonyms;
    private final Set<String> xrefs;
    private final String chromose;
    private final String mapLocation;
    private final String description;
    private final String type;
    private final String symbolNomenclatureAuthority;
    private final String fullNameNomenclatureAuthority;
    private final String modificationDate;

    public Gene( GeneDTO dto ) {
        this.geneId = dto.getGeneId();
        this.symbol = dto.getSymbol();
        this.synonyms = ImmutableSet.copyOf( dto.getSynonyms().split( "\\|" ) );
        this.xrefs = ImmutableSet.copyOf( dto.getXrefs().split( "\\|" ) );
        this.chromose = dto.getChromose();
        this.mapLocation = dto.getMapLocation();
        this.description = dto.getDescription();
        this.type = dto.getType();
        this.symbolNomenclatureAuthority = dto.getSymbolNomenclatureAuthority();
        this.fullNameNomenclatureAuthority = dto.getFullNameNomenclatureAuthority();
        this.modificationDate = dto.getModificationDate();
    }

    public Integer getGeneId() {
        return geneId;
    }

    public String getSymbol() {
        return symbol;
    }

    public Set<String> getSynonyms() {
        return synonyms;
    }

    public Set<String> getXrefs() {
        return xrefs;
    }

    public String getChromose() {
        return chromose;
    }

    public String getMapLocation() {
        return mapLocation;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getSymbolNomenclatureAuthority() {
        return symbolNomenclatureAuthority;
    }

    public String getFullNameNomenclatureAuthority() {
        return fullNameNomenclatureAuthority;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public String getLabel() {
        return symbol + " - " + description;
    }

    @Override
    public String toString() {
        return "Gene [geneId=" + geneId + ", symbol=" + symbol + ", description=" + description + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( geneId == null ) ? 0 : geneId.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Gene other = ( Gene ) obj;
        if ( geneId == null ) {
            if ( other.geneId != null ) return false;
        } else if ( !geneId.equals( other.geneId ) ) return false;
        return true;
    }

    @Override
    public int compareTo( Gene o ) {
        return symbol.compareTo( o.getSymbol() );
    }

}
