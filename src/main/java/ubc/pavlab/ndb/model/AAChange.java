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
            this.gene = aaChangeArray.length > 0 ? aaChangeArray[0] : "N/A";
            this.transcript = aaChangeArray.length  > 1 ? aaChangeArray[1] : "N/A";
            this.context = aaChangeArray.length  > 2 ? aaChangeArray[2] : "N/A";
            this.hgvsC = aaChangeArray.length  > 3 ? aaChangeArray[3] : "N/A";
            this.hgvsP = aaChangeArray.length  > 4 ? aaChangeArray[4] : "N/A";

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
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        AAChange aaChange = (AAChange) o;

        if ( gene != null ? !gene.equals( aaChange.gene ) : aaChange.gene != null ) return false;
        if ( transcript != null ? !transcript.equals( aaChange.transcript ) : aaChange.transcript != null )
            return false;
        if ( context != null ? !context.equals( aaChange.context ) : aaChange.context != null ) return false;
        if ( hgvsC != null ? !hgvsC.equals( aaChange.hgvsC ) : aaChange.hgvsC != null ) return false;
        return hgvsP != null ? hgvsP.equals( aaChange.hgvsP ) : aaChange.hgvsP == null;
    }

    @Override
    public int hashCode() {
        int result = gene != null ? gene.hashCode() : 0;
        result = 31 * result + (transcript != null ? transcript.hashCode() : 0);
        result = 31 * result + (context != null ? context.hashCode() : 0);
        result = 31 * result + (hgvsC != null ? hgvsC.hashCode() : 0);
        result = 31 * result + (hgvsP != null ? hgvsP.hashCode() : 0);
        return result;
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
