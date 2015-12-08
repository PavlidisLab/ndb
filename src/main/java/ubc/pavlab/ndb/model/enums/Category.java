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

package ubc.pavlab.ndb.model.enums;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public enum Category {
    
    frameshiftInsertion("frameshift insertion", 1, "an insertion of one or more nucleotides that cause frameshift changes in protein coding sequence", "frameshift_elongation (SO:0001909)"),
    frameshiftDeletion("frameshift deletion", 2, "a deletion of one or more nucleotides that cause frameshift changes in protein coding sequence", "frameshift_truncation (SO:0001910)"),
    frameshiftBlockSubstitution("frameshift block substitution", 3, "a block substitution of one or more nucleotides that cause frameshift changes in protein coding sequence", "frameshift_variant (SO:0001589)"),
    frameshiftSubstitution("frameshift substitution", 3, "a substitution of one or more nucleotides that cause frameshift changes in protein coding sequence", "frameshift_variant (SO:0001589)"),
    nonframeshiftInsertion("nonframeshift insertion", 6, "an insertion of 3 or multiples of 3 nucleotides that do not cause frameshift changes in protein coding sequence", "inframe_insertion (SO:0001821)"),
    nonframeshiftDeletion("nonframeshift deletion", 7, "a deletion of 3 or mutliples of 3 nucleotides that do not cause frameshift changes in protein coding sequence", "inframe_deletion (SO:0001822)"),
    nonframeshiftBlockSubstitution("nonframeshift block substitution", 8, "a block substitution of one or more nucleotides that do not cause frameshift changes in protein coding sequence", "inframe_variant (SO:0001650)"),
    nonframeshiftSubstitution("nonframeshift substitution", 8, "a substitution of one or more nucleotides that do not cause frameshift changes in protein coding sequence", "inframe_variant (SO:0001650)"),
    nonsynonymousSNV("nonsynonymous SNV", 9, "a single nucleotide change that cause an amino acid change", "missense_variant (SO:0001583)"),
    stopgain("stopgain", 4, "a nonsynonymous SNV, frameshift insertion/deletion, nonframeshift insertion/deletion or block substitution that lead to the immediate creation of stop codon at the variant site. For frameshift mutations, the creation of stop codon downstream of the variant will not be counted as 'stopgain'!", "stop_gained (SO:0001587)"),
    stoploss("stoploss", 5, "a nonsynonymous SNV, frameshift insertion/deletion, nonframeshift insertion/deletion or block substitution that lead to the immediate elimination of stop codon at the variant site", "stop_lost (SO:0001578)"),
    synonymousSNV("synonymous SNV", 10, "a single nucleotide change that does not cause an amino acid change", "synonymous_variant (SO:0001819)"),
    unknown("unknown", 11, "unknown function (due to various errors in the gene structure definition in the database file)", "sequence_variant (SO:0001060)");

    private String label;
    private Integer precedence;
    private String description;
    private String sequenceOntology;

    private Category( String label, Integer precedence, String description, String sequenceOntology ) {
        this.label = label;
        this.precedence = precedence;
        this.description = description;
        this.sequenceOntology = sequenceOntology;
        
    }

    public String getLabel() {
        return label;
    }

    public Integer getPrecedence() {
        return precedence;
    }

    public String getDescription() {
        return description;
    }

    public String getSequenceOntology() {
        return sequenceOntology;
    }
    
    @Override
    public String toString() {
        return this.getLabel();
    }
    
    public static Category getEnum(String value) {
        
        if ( value == null || value.trim().equals( "" )) {
            return null;
        }
        String trimValue = value.trim();
        for(Category v : values())
            if(v.getLabel().equalsIgnoreCase(trimValue)) return v;
        throw new IllegalArgumentException();
    }

}
