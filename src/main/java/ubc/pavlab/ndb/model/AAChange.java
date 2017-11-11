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

/**
 * Represents an Amino Acid change (e.g. MYEF2:NM_001301210:exon10:c.C1019T:p.P340L )
 * 
 * @author mjacobson
 * @version $Id$
 */
public class AAChange {

    private final String gene;
    private final String transcript;
    private final String context;
    private final String hgvsC;
    private final String hgvsP;

    public AAChange( String gene, String transcript, String context, String hgvsC, String hgvsP ) {
        // Modular constructor
        this.gene = gene;
        this.transcript = transcript;
        this.context = context;
        this.hgvsC = hgvsC;
        this.hgvsP = hgvsP;
    }

    public AAChange( String aaChangeString ) throws ArrayIndexOutOfBoundsException {
        // By AAChange string constructor
        String [] aaChangeArray = null;
        try {
            aaChangeArray = aaChangeString.split(":");
            this.gene = aaChangeArray[0];
            this.transcript = aaChangeArray[1];
            this.context = aaChangeArray[2];
            this.hgvsC = aaChangeArray[3];
            if (aaChangeArray.length == 5) {
                this.hgvsP = aaChangeArray[4];
            } else {
                this.hgvsP = "N/A";
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception thrown trying to split 'aaChangeString' :" + aaChangeString);
            throw e;
        }
    }


    @Override
    public String toString() {
        return "AAChange [gene=" + gene + ", transcript=" + transcript + ", context=" + context + ", hgvsC="
               + hgvsC + ", hgvsP=" + hgvsP +  "]";
    }

    @Override
    public boolean equals( Object obj ) {

        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        AAChange other = (AAChange) obj;
        if ( gene.toString() == null ) {
            if ( other.gene.toString() != null ) return false;
        } else if ( !gene.toString().equals( other.gene.toString() ) ) return false;
        return true;
    }


    public String getGene() {
        return gene;
    }
    public String getTranscript() {
        return transcript;
    }

    public String getContext() {
        return context;
    }

    public String getHgvsC() {
        return hgvsC;
    }

    public String getHgvsP() {
        return hgvsP;
    }


}
