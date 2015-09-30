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
public class LOFBreakdown {
    private final Integer id;
    private final Gene gene;
    private final Double score; //gene score: (TotalLoF + (CADDtotal/30)) / (max(ExACgeneLoF, annoExACgeneLoF)+20)
    private final Integer countDenovo; //number of denovo LoF mutations (nonsense and frameshift)
    private final Integer countOther; //number of non-denovo LoF mutations (nonsense and frameshift)
    private final Integer countLOF; //total LoF mutations (nonsense and frameshift)
    private final Integer denovoMissInframeDeletion; //number of denovo missense mutations (missense and in-frame deletions)
    private final Integer otherMissInframeDeletion; //number of non denovo missense mutations (missense and in-frame deletions)
    private final Double missInframeCaddTotal; //total CADD score for missense mutations

    private final Integer variantCntExac01; //number variants in ExAC with freq > .01
    private final Integer variantCntExac001; //number variants in ExAC with freq > .001
    private final Integer variantCntExac0001; //number variants in ExAC with freq > .0001
    private final Integer lofCntExacPaperName; //number of high-confidence LoF variants in ExAC (based on reported gene name)
    private final Integer lofCntExacAnnovarName; //number of high-confidence LoF variants in ExAC (based on annoVar gene name)

    private final String sfariScorePaperName; //SFARI ASD gene score (based on reported gene name)
    private final String sfariScoreAnnovarName; //SFARI ASD gene score (based on anoVar gene name)
    private final Boolean missInframeInSSC; //missense mutations in SSC?

    private final Boolean associatedPSD; //Post-synaptic density associated

    private final Boolean interactionFMRPL; //FMRP associated
    private final Boolean interactionPTEN; //protein interaction with PTEN
    private final Boolean interactionNLGN3; //protein interaction with NLGN3

    public LOFBreakdown( Integer id, Gene gene, Double score, Integer countDenovo, Integer countOther, Integer countLOF,
            Integer denovoMissInframeDeletion, Integer otherMissInframeDeletion, Double missInframeCaddTotal,
            Integer variantCntExac01, Integer variantCntExac001, Integer variantCntExac0001,
            Integer lofCntExacPaperName, Integer lofCntExacAnnovarName, String sfariScorePaperName,
            String sfariScoreAnnovarName, Boolean missInframeInSSC, Boolean associatedPSD, Boolean interactionFMRPL,
            Boolean interactionPTEN, Boolean interactionNLGN3 ) {
        super();
        this.id = id;
        this.gene = gene;
        this.score = score;
        this.countDenovo = countDenovo;
        this.countOther = countOther;
        this.countLOF = countLOF;
        this.denovoMissInframeDeletion = denovoMissInframeDeletion;
        this.otherMissInframeDeletion = otherMissInframeDeletion;
        this.missInframeCaddTotal = missInframeCaddTotal;
        this.variantCntExac01 = variantCntExac01;
        this.variantCntExac001 = variantCntExac001;
        this.variantCntExac0001 = variantCntExac0001;
        this.lofCntExacPaperName = lofCntExacPaperName;
        this.lofCntExacAnnovarName = lofCntExacAnnovarName;
        this.sfariScorePaperName = sfariScorePaperName;
        this.sfariScoreAnnovarName = sfariScoreAnnovarName;
        this.missInframeInSSC = missInframeInSSC;
        this.associatedPSD = associatedPSD;
        this.interactionFMRPL = interactionFMRPL;
        this.interactionPTEN = interactionPTEN;
        this.interactionNLGN3 = interactionNLGN3;
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
        LOFBreakdown other = ( LOFBreakdown ) obj;
        if ( id == null ) {
            if ( other.id != null ) return false;
        } else if ( !id.equals( other.id ) ) return false;
        return true;
    }

    public Integer getId() {
        return id;
    }

    public Gene getGene() {
        return gene;
    }

    public Double getScore() {
        return score;
    }

    public Integer getCountDenovo() {
        return countDenovo;
    }

    public Integer getCountOther() {
        return countOther;
    }

    public Integer getCountLOF() {
        return countLOF;
    }

    public Integer getDenovoMissInframeDeletion() {
        return denovoMissInframeDeletion;
    }

    public Integer getOtherMissInframeDeletion() {
        return otherMissInframeDeletion;
    }

    public Double getMissInframeCaddTotal() {
        return missInframeCaddTotal;
    }

    public Integer getVariantCntExac01() {
        return variantCntExac01;
    }

    public Integer getVariantCntExac001() {
        return variantCntExac001;
    }

    public Integer getVariantCntExac0001() {
        return variantCntExac0001;
    }

    public Integer getLofCntExacPaperName() {
        return lofCntExacPaperName;
    }

    public Integer getLofCntExacAnnovarName() {
        return lofCntExacAnnovarName;
    }

    public String getSfariScorePaperName() {
        return sfariScorePaperName;
    }

    public String getSfariScoreAnnovarName() {
        return sfariScoreAnnovarName;
    }

    public Boolean getMissInframeInSSC() {
        return missInframeInSSC;
    }

    public Boolean getAssociatedPSD() {
        return associatedPSD;
    }

    public Boolean getInteractionFMRPL() {
        return interactionFMRPL;
    }

    public Boolean getInteractionPTEN() {
        return interactionPTEN;
    }

    public Boolean getInteractionNLGN3() {
        return interactionNLGN3;
    }

}
