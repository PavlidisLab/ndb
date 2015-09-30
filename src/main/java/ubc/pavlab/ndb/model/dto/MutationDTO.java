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
public final class MutationDTO {
    private final Integer id;
    private final Integer paper_id;
    private final String sample_identifier;
    private final String chromosome;
    private final Integer hg19_start;
    private final Integer hg19_stop;
    private final String ref;
    private final String alt;
    private final Integer gene_id;
    private final String mutation_effect;
    private final String code_change;
    private final String prot_change;
    private final String qvalue;
    private final String sift;
    private final String polyphen;
    private final Boolean goodmut;

    public MutationDTO( Integer id, Integer paper_id, String sample_identifier, String chromosome, Integer hg19_start,
            Integer hg19_stop, String ref, String alt, Integer gene_id, String mutation_effect, String code_change,
            String prot_change, String qvalue, String sift, String polyphen, Boolean goodmut ) {
        super();
        this.id = id;
        this.paper_id = paper_id;
        this.sample_identifier = sample_identifier;
        this.chromosome = chromosome;
        this.hg19_start = hg19_start;
        this.hg19_stop = hg19_stop;
        this.ref = ref;
        this.alt = alt;
        this.gene_id = gene_id;
        this.mutation_effect = mutation_effect;
        this.code_change = code_change;
        this.prot_change = prot_change;
        this.qvalue = qvalue;
        this.sift = sift;
        this.polyphen = polyphen;
        this.goodmut = goodmut;
    }

    public Integer getId() {
        return this.id;
    }

    public Integer getPaper_id() {
        return paper_id;
    }

    public String getSample_identifier() {
        return sample_identifier;
    }

    public String getChromosome() {
        return chromosome;
    }

    public Integer getHg19_start() {
        return hg19_start;
    }

    public Integer getHg19_stop() {
        return hg19_stop;
    }

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }

    public Integer getGene_id() {
        return gene_id;
    }

    public String getMutation_effect() {
        return mutation_effect;
    }

    public String getCode_change() {
        return code_change;
    }

    public String getProt_change() {
        return prot_change;
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

}
