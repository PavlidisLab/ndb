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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * TODO Document Me
 * 
 * @author mbelmadani
 * @version $Id$
 */
public class SourceMutation {
    private final Integer id;
    private final Paper paper;
    private Map<String, String> source = new Hashtable<String, String>();
    private List<Entry<String, String>> entries;

    public Map<String, String> getSource() {
        return source;
    }

    public void setSource( Hashtable<String, String> source ) {
        this.source = source;
    }

    public SourceMutation( int id, Paper paper, Hashtable<String, String> raw ) {
        this.id = id;
        this.paper = paper;

        if ( raw == null ) {
            this.makeMockRaw();
        } else {
            this.source = raw;
        }
    }

    public void makeMockRaw() {
        /*
         * Chr (hg18) Pos (hg18) Chr (hg19) Pos (hg19) Ref Alt rsIDv132 dbSNP Accension Gene Brain_expressed Effect
         * Initial_Codon Initial_AA New_Codon New_AA AA_Change BP_Change GranthamScore SIFT SIFT Score Median
         * Information Content # Seqs at position PolyPhen2 pph2_prob pph2_FPR pph2_TPR PhyloP GERP Risk 10 108437954
         * chr10 108447964 C T 0 Novel NM_001013031 SORCS1 Yes Missense GTG V ATG M V516M G1546A 21 TOLERATED 0.07 3.02
         * 20 possiblydamaging 0.689 0.0801 0.863 1.642 5.71 0.000107685
         */

        Hashtable<String, String> raw = new Hashtable<String, String>();
        raw.put( "Child_ID", "11000.p1" );
        raw.put( "Family_Type", "Quartet" );
        raw.put( "Chr", "(hg18)" );
        raw.put( "Pos", "(hg18)" );
        raw.put( "Chr", "(hg19)" );
        raw.put( "Pos", "(hg19)" );
        raw.put( "Ref", "C" );
        raw.put( "Alt", "T" );
        raw.put( "rsIDv132", "0" );
        raw.put( "dbSNP", "Novel" );
        raw.put( "Accension", "NM_001013031" );
        raw.put( "Gene", "SORCS1" );
        raw.put( "Brain_expressed", "Yes" );
        raw.put( "Effect", "Missense" );
        raw.put( "Initial_Codon", "GTG" );
        raw.put( "Initial_AA", "V" );
        raw.put( "New_Codon", "ATG" );
        raw.put( "New_AA", "M" );
        raw.put( "AA_Change", "V516M" );
        raw.put( "BP_Change", "G1546A" );
        raw.put( "GranthamScore", "21" );
        raw.put( "SIFT", "TOLERATED" );
        raw.put( "SIFT", "Score" );
        raw.put( "Median", "Information" );
        raw.put( "PolyPhen2", "possiblydamaging" );
        raw.put( "pph2_prob", "0.689" );
        raw.put( "pph2_FPR", "0.0801" );
        raw.put( "pph2_TPR", "0.863" );
        raw.put( "PhyloP", "1.642" );
        raw.put( "GERP", "5.71" );
        raw.put( "Risk", "0.000107685" );

        this.source = raw;
        entries = new ArrayList<>( raw.entrySet() );
    }

    public Integer getId() {
        return id;
    }

    public Paper getPaper() {
        return paper;
    }

    @Override
    public String toString() {
        return "Raw Mutation [id=" + id + ", paper=" + paper + ", raw=" + source.toString() + "]";
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
        SourceMutation other = ( SourceMutation ) obj;
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