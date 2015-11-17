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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Event variant grouping
 * 
 * @author mbelmadani
 * @version $Id$
 */
public class Event {
    private Integer eventId;
    private String chromosome;
    private String ref;
    private String alt;
    private String location;
    private String effects;
    private Integer subjectId;
    private int startHg19;
    private int stopHg19;

    private List<Variant> variants;

    private Map<String, Gene> symbols;

    private Map<String, String> authors;

    public Event( List<Variant> variants ) {
        this.variants = variants;
        this.assignKeyProperties();

    }

    private void getMinMaxLocations() {
        int max = -1, min = -1;

        for ( Variant v : this.variants ) {
            if ( min == -1 || min > v.getStartHg19() ) {
                min = v.getStartHg19();
            }
            if ( max < v.getStopHg19() ) {
                max = v.getStopHg19();
            }
        }

        this.startHg19 = min;
        this.stopHg19 = max;

    }

    private void assignKeyProperties() {
        /*
         * Get first element and assign properties to the individual properties TODO: Figure out the best properties to
         * obtain here
         */
        Variant trunk = variants.get( 0 );
        this.eventId = trunk.getEventId();
        this.chromosome = trunk.getChromosome();
        this.ref = trunk.getRef();
        this.alt = trunk.getAlt();
        this.location = "chr" + trunk.getChromosome() + ":" + trunk.getStartHg19() + "-" + trunk.getStopHg19();
        this.effects = trunk.getAnnovar().getGenes().get( 0 ).getXrefs().toString();
        this.subjectId = trunk.getSubjectId();

        this.getMinMaxLocations();

        // this.symbol = trunk.getAnnovar().getGenes().get( 0 ).getSymbol();

        this.symbols = new HashMap<>();
        for ( Gene g : trunk.getAnnovar().getGenes() ) {
            if ( !this.symbols.containsKey( g.getSymbol() ) ) {
                this.symbols.put( g.getSymbol(), g );
            }
        }

        this.authors = new HashMap<>();
        for ( Variant v : this.variants ) {
            Paper p = v.getPaper();
            this.authors.put( p.getAuthor(), p.getUrl() );
        }
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants( List<Variant> variants ) {
        this.variants = variants;
    }

    public String getRef() {
        return ref;
    }

    public void setRef( String ref ) {
        this.ref = ref;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt( String alt ) {
        this.alt = alt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation( String location ) {
        this.location = location;
    }

    public String getEffects() {
        return effects;
    }

    public void setEffects( String effects ) {
        this.effects = effects;
    }

    public Map<String, String> getAuthors() {
        return authors;
    }

    public void setAuthors( Map<String, String> authors ) {
        this.authors = authors;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId( Integer eventId ) {
        this.eventId = eventId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId( int sampleId ) {
        this.subjectId = sampleId;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome( String chromosome ) {
        this.chromosome = chromosome;
    }

    public int getStartHg19() {
        return startHg19;
    }

    public void setStartHg19( int startHg19 ) {
        this.startHg19 = startHg19;
    }

    public int getStopHg19() {
        return stopHg19;
    }

    public void setStopHg19( int stopHg19 ) {
        this.stopHg19 = stopHg19;
    }

    public Map<String, Gene> getSymbols() {
        return symbols;
    }

    public void setSymbols( Map<String, Gene> symbols ) {
        this.symbols = symbols;
    }

}
