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

package ubc.pavlab.ndb.model.dto;

import ubc.pavlab.ndb.model.Gene;

/**
 * Data Transfer Object for {@link Gene}
 * 
 * @author mjacobson
 * @version $Id$
 */
public final class GeneDTO {
    private final Integer geneId;
    private final String symbol;
    private final String synonyms;
    private final String xrefs;
    private final String chromose;
    private final String mapLocation;
    private final String description;
    private final String type;
    private final String symbolNomenclatureAuthority;
    private final String fullNameNomenclatureAuthority;
    private final String modificationDate;

    public GeneDTO( Integer geneId, String symbol, String synonyms, String xrefs, String chromose, String mapLocation,
            String description, String type, String symbolNomenclatureAuthority, String fullNameNomenclatureAuthority,
            String modificationDate ) {
        this.geneId = geneId;
        this.symbol = symbol;
        this.synonyms = synonyms;
        this.xrefs = xrefs;
        this.chromose = chromose;
        this.mapLocation = mapLocation;
        this.description = description;
        this.type = type;
        this.symbolNomenclatureAuthority = symbolNomenclatureAuthority;
        this.fullNameNomenclatureAuthority = fullNameNomenclatureAuthority;
        this.modificationDate = modificationDate;
    }

    public Integer getGeneId() {
        return geneId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public String getXrefs() {
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

}
