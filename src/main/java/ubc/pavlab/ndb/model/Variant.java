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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ubc.pavlab.ndb.model.dto.VariantDTO;
import ubc.pavlab.ndb.model.enums.Category;

/**
 * Represents a variant-subject pairing
 * 
 * @author mjacobson
 * @version $Id$
 */
public class Variant {

    private final Integer id;
    private final Integer rawVariantId;
    private final Integer eventId;
    private final Integer subjectId;
    private final String sampleId;
    private final String chromosome;
    private final Integer startHg19;
    private final Integer stopHg19;
    private final String ref;
    private final String alt;
    private final Annovar annovar;
    private final Paper paper;

    private final List<Gene> genes;
    private final Category category;
    private final String geneDetail;
    private final String func;
    private final List<String> aaChanges;
    private final String cytoband;
    private final String inheritance;
    private final String lof;

    private final String validation;
    private final String validationMethod;

    private final List<RawKV> rawKV;

    public Variant( VariantDTO dto, Annovar annovar, List<RawKV> rawKV, Paper paper, List<Gene> genes,
            Category category ) {
        this.id = dto.getId();
        this.rawVariantId = dto.getRawVariantId();
        this.eventId = dto.getEventId();
        this.subjectId = dto.getSubjectId();
        this.sampleId = dto.getSampleId();
        this.chromosome = dto.getChromosome();
        this.startHg19 = dto.getStartHg19();
        this.stopHg19 = dto.getStopHg19();
        this.ref = dto.getRef();
        this.alt = dto.getAlt();
        this.inheritance = dto.getInheritance();
        this.validation = dto.getValidation();
        this.validationMethod = dto.getValidationMethod();
        this.lof = dto.getLoF();

        this.annovar = annovar;
        this.paper = paper;

        this.genes = genes;
        this.category = category;
        this.geneDetail = dto.getGeneDetail();
        this.func = dto.getFunc();

        this.aaChanges = StringUtils.isBlank( dto.getAaChange() ) ? new ArrayList<String>()
                : Arrays.asList( dto.getAaChange().split( "," ) );

        this.cytoband = dto.getCytoband();

        this.rawKV = rawKV;
    }

    public Integer getId() {
        return id;
    }

    public Paper getPaper() {
        return paper;
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

    public Integer getStart() {
        return startHg19;
    }

    public Integer getStopHg19() {
        return stopHg19;
    }

    public Integer getStop() {
        return stopHg19;
    }

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }

    public Annovar getAnnovar() {
        return annovar;
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public Category getCategory() {
        return category;
    }

    public String getGeneDetail() {
        return geneDetail;
    }

    public String getFunc() {
        return func;
    }

    public List<String> getAaChanges() {
        return aaChanges;
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

    public List<RawKV> getRawKV() {
        return rawKV;
    }

    @Override
    public String toString() {
        return "Variant [id=" + id + ", paper=" + paper.getPaperKey() + ", eventId=" + eventId + ", subjectId="
                + subjectId + ", sampleId=" + sampleId + ", chromosome=" + chromosome + ", startHg19=" + startHg19
                + ", stopHg19=" + stopHg19 + ", ref=" + ref + ", alt=" + alt + "]";
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
        Variant other = ( Variant ) obj;
        if ( id == null ) {
            if ( other.id != null ) return false;
        } else if ( !id.equals( other.id ) ) return false;
        return true;
    }

}
