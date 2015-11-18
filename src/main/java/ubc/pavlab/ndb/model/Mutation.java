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
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class Mutation {
    private final Integer id;
    private final Paper paper;
    private final Gene gene;
    private final String sampleIdentifier;
    private final String chromosome;
    private final Integer startHg19;
    private final Integer stopHg19;
    private final String ref;
    private final String alt;
    private final String mutationEffect;
    private final String codeChange;
    private final String protChange;
    private final String qvalue;
    private final String sift;
    private final String polyphen;
    private final Boolean goodmut;

    public Mutation( Integer id, Paper paper, Gene gene, String sampleIdentifier, String chromosome, Integer startHg19,
            Integer stopHg19, String ref, String alt, String mutationEffect, String codeChange, String protChange,
            String qvalue, String sift, String polyphen, Boolean goodmut ) {
        super();
        this.id = id;
        this.paper = paper;
        this.gene = gene;
        this.sampleIdentifier = sampleIdentifier;
        this.chromosome = chromosome;
        this.startHg19 = startHg19;
        this.stopHg19 = stopHg19;
        this.ref = ref;
        this.alt = alt;
        this.mutationEffect = mutationEffect;
        this.codeChange = codeChange;
        this.protChange = protChange;
        this.qvalue = qvalue;
        this.sift = sift;
        this.polyphen = polyphen;
        this.goodmut = goodmut;
    }

    public Integer getId() {
        return id;
    }

    public Paper getPaper() {
        return paper;
    }

    public Gene getGene() {
        return gene;
    }

    public String getSampleIdentifier() {
        return sampleIdentifier;
    }

    public String getChromosome() {
        return chromosome;
    }

    public Integer getstartHg19() {
        return startHg19;
    }

    public Integer getstopHg19() {
        return stopHg19;
    }

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }

    public String getMutationEffect() {
        return mutationEffect;
    }

    public String getCodeChange() {
        return codeChange;
    }

    public String getProtChange() {
        return protChange;
    }

    public String getQvalue() {
        return qvalue;
    }

    public String getSift() {
        return sift;
    }

    public String getPolyphen() {
        return polyphen;
    }

    public Boolean getGoodmut() {
        return goodmut;
    }

    @Override
    public String toString() {
        return "Mutation [id=" + id + ", paper=" + paper + ", gene=" + gene + ", sampleIdentifier=" + sampleIdentifier
                + "]";
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
        Mutation other = ( Mutation ) obj;
        if ( id == null ) {
            if ( other.id != null ) return false;
        } else if ( !id.equals( other.id ) ) return false;
        return true;
    }

}