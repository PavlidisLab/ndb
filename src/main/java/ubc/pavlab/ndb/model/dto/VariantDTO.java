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
public final class VariantDTO {
    private final Integer id;
    private final Integer paperId;
    private final Integer rawVariantId;
    private final Integer eventId;
    private final Integer subjectId;
    private final String sampleId;
    private final String chromosome;
    private final Integer startHg19;
    private final Integer stopHg19;
    private final String ref;
    private final String alt;

    private final String gene;
    private final String category;
    private final String geneDetail;
    private final String func;
    private final String aaChange;
    private final String cytoband;

    private final String inheritance;
    private final String lof;

    private final String validation;
    private final String validationMethod;

    public VariantDTO( Integer id, Integer paperId, Integer rawVariantId, Integer eventId, Integer subjectId,
            String sampleId, String chromosome, Integer startHg19, Integer stopHg19, String ref, String alt,
            String gene, String category, String geneDetail, String func, String aaChange, String cytoband,
            String inheritance, String lof, String validation, String validationMethod ) {
        this.id = id;
        this.paperId = paperId;
        this.rawVariantId = rawVariantId;
        this.eventId = eventId;
        this.subjectId = subjectId;
        this.sampleId = sampleId;
        this.chromosome = chromosome;
        this.startHg19 = startHg19;
        this.stopHg19 = stopHg19;
        this.ref = ref;
        this.alt = alt;
        this.gene = gene;
        this.category = category;
        this.geneDetail = geneDetail;
        this.func = func;
        this.aaChange = aaChange;
        this.cytoband = cytoband;
        this.inheritance = inheritance;
        this.lof = lof;
        this.validation = validation;
        this.validationMethod = validationMethod;

    }

    public Integer getId() {
        return id;
    }

    public Integer getPaperId() {
        return paperId;
    }

    public Integer getRawVariantId() {
        return rawVariantId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public String getSampleId() {
        return sampleId;
    }

    public String getChromosome() {
        return chromosome;
    }

    public Integer getStartHg19() {
        return startHg19;
    }

    public Integer getStopHg19() {
        return stopHg19;
    }

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }

    public String getGene() {
        return gene;
    }

    public String getCategory() {
        return category;
    }

    public String getGeneDetail() {
        return geneDetail;
    }

    public String getFunc() {
        return func;
    }

    public String getAaChange() {
        return aaChange;
    }

    public String getCytoband() {
        return cytoband;
    }

    public String getInheritance() {
        return inheritance;
    }

    public String getValidation() {
        return validation;
    }

    public String getValidationMethod() {
        return validationMethod;
    }

    public String getLoF() {
        return lof;
    }

}
