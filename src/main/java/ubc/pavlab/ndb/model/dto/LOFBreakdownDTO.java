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

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public final class LOFBreakdownDTO {
    private final Integer id;
    private final Integer gene_id;
    private final Double score;
    private final Integer count_denovo;
    private final Integer count_other;
    private final Integer count_lof;
    private final Integer denovo_miss_inframe_deletion;
    private final Integer other_miss_inframe_deletion;
    private final Double miss_inframe_cadd_total;
    private final Boolean miss_inframe_in_SSC;
    private final Integer exac_frequency_gt_point01;
    private final Integer exac_frequency_gt_point001;
    private final Integer exac_frequency_gt_point0001;
    private final Boolean postsynaptic_density_associated;
    private final Boolean interaction_with_FMRPL;
    private final Integer count_lof_in_exac_genename;
    private final Integer count_lof_in_exac_annovarname;
    private final String SFARI_ASD_gene_score_genename;
    private final String SFARI_ASD_gene_score_annovarname;
    private final Boolean interaction_PTEN;
    private final Boolean interaction_NLGN3;

    public LOFBreakdownDTO( Integer id, Integer gene_id, Double score, Integer count_denovo, Integer count_other,
            Integer count_lof, Integer denovo_miss_inframe_deletion, Integer other_miss_inframe_deletion,
            Double miss_inframe_cadd_total, Boolean miss_inframe_in_SSC, Integer exac_frequency_gt_point01,
            Integer exac_frequency_gt_point001, Integer exac_frequency_gt_point0001,
            Boolean postsynaptic_density_associated, Boolean interaction_with_FMRPL, Integer count_lof_in_exac_genename,
            Integer count_lof_in_exac_annovarname, String sFARI_ASD_gene_score_genename,
            String sFARI_ASD_gene_score_annovarname, Boolean interaction_PTEN, Boolean interaction_NLGN3 ) {
        super();
        this.id = id;
        this.gene_id = gene_id;
        this.score = score;
        this.count_denovo = count_denovo;
        this.count_other = count_other;
        this.count_lof = count_lof;
        this.denovo_miss_inframe_deletion = denovo_miss_inframe_deletion;
        this.other_miss_inframe_deletion = other_miss_inframe_deletion;
        this.miss_inframe_cadd_total = miss_inframe_cadd_total;
        this.miss_inframe_in_SSC = miss_inframe_in_SSC;
        this.exac_frequency_gt_point01 = exac_frequency_gt_point01;
        this.exac_frequency_gt_point001 = exac_frequency_gt_point001;
        this.exac_frequency_gt_point0001 = exac_frequency_gt_point0001;
        this.postsynaptic_density_associated = postsynaptic_density_associated;
        this.interaction_with_FMRPL = interaction_with_FMRPL;
        this.count_lof_in_exac_genename = count_lof_in_exac_genename;
        this.count_lof_in_exac_annovarname = count_lof_in_exac_annovarname;
        SFARI_ASD_gene_score_genename = sFARI_ASD_gene_score_genename;
        SFARI_ASD_gene_score_annovarname = sFARI_ASD_gene_score_annovarname;
        this.interaction_PTEN = interaction_PTEN;
        this.interaction_NLGN3 = interaction_NLGN3;
    }

    public Integer getId() {
        return id;
    }

    public Integer getGene_id() {
        return gene_id;
    }

    public Double getScore() {
        return score;
    }

    public Integer getCount_denovo() {
        return count_denovo;
    }

    public Integer getCount_other() {
        return count_other;
    }

    public Integer getCount_lof() {
        return count_lof;
    }

    public Integer getDenovo_miss_inframe_deletion() {
        return denovo_miss_inframe_deletion;
    }

    public Integer getOther_miss_inframe_deletion() {
        return other_miss_inframe_deletion;
    }

    public Double getMiss_inframe_cadd_total() {
        return miss_inframe_cadd_total;
    }

    public Boolean getMiss_inframe_in_SSC() {
        return miss_inframe_in_SSC;
    }

    public Integer getExac_frequency_gt_point01() {
        return exac_frequency_gt_point01;
    }

    public Integer getExac_frequency_gt_point001() {
        return exac_frequency_gt_point001;
    }

    public Integer getExac_frequency_gt_point0001() {
        return exac_frequency_gt_point0001;
    }

    public Boolean getPostsynaptic_density_associated() {
        return postsynaptic_density_associated;
    }

    public Boolean getInteraction_with_FMRPL() {
        return interaction_with_FMRPL;
    }

    public Integer getCount_lof_in_exac_genename() {
        return count_lof_in_exac_genename;
    }

    public Integer getCount_lof_in_exac_annovarname() {
        return count_lof_in_exac_annovarname;
    }

    public String getSFARI_ASD_gene_score_genename() {
        return SFARI_ASD_gene_score_genename;
    }

    public String getSFARI_ASD_gene_score_annovarname() {
        return SFARI_ASD_gene_score_annovarname;
    }

    public Boolean getInteraction_PTEN() {
        return interaction_PTEN;
    }

    public Boolean getInteraction_NLGN3() {
        return interaction_NLGN3;
    }
}
