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

import ubc.pavlab.ndb.model.dto.AnnovarDTO;

/**
 * Represents the annovar information for a specific @Variant
 * 
 * @author mjacobson
 * @version $Id$
 */
public class Annovar {
    private final Integer id;
    private final Integer variantId;
    private final String genomicSuperDups;
    private final Double esp6500siv2All;
    private final Double octAll1000g2014;
    private final Double octAfr1000g2014;
    private final Double octEas1000g2014;
    private final Double octEur1000g2014;
    private final String snp138;
    private final Double siftScore;
    private final String siftPred;
    private final Double polyphen2hdivScore;
    private final String polyphen2hdivPred;
    private final Double polyphen2hvarScore;
    private final String polyphen2hvarPred;
    private final Double lrtScore;
    private final String lrtPred;
    private final Double mutationTasterScore;
    private final String mutationTasterPred;
    private final Double mutationAssessorScore;
    private final String mutationAssessorPred;
    private final Double fathmmScore;
    private final String fathmmPred;
    private final Double radialSVMScore;
    private final String radialSVMPred;
    private final Double lrScore;
    private final String lrPred;
    private final Double vest3Score;
    private final Double caddRaw;
    private final Double caddPhred;
    private final Double gerpRs;
    private final Double phyloP46wayPlacental;
    private final Double phyloP100wayVertebrate;
    private final Double siphy29wayLogOdds;
    private final Double exac03;
    private final String clinvar20150629;

    public Annovar( AnnovarDTO dto ) {
        this.id = dto.getId();
        this.variantId = dto.getVariantId();
        this.genomicSuperDups = dto.getGenomicSuperDups();
        this.esp6500siv2All = dto.getEsp6500siv2All();
        this.octAll1000g2014 = dto.getOctAll1000g2014();
        this.octAfr1000g2014 = dto.getOctAfr1000g2014();
        this.octEas1000g2014 = dto.getOctEas1000g2014();
        this.octEur1000g2014 = dto.getOctEur1000g2014();
        this.snp138 = dto.getSnp138();
        this.siftScore = dto.getSiftScore();
        this.siftPred = dto.getSiftPred();
        this.polyphen2hdivScore = dto.getPolyphen2hdivScore();
        this.polyphen2hdivPred = dto.getPolyphen2hdivPred();
        this.polyphen2hvarScore = dto.getPolyphen2hvarScore();
        this.polyphen2hvarPred = dto.getPolyphen2hvarPred();
        this.lrtScore = dto.getLrtScore();
        this.lrtPred = dto.getLrtPred();
        this.mutationTasterScore = dto.getMutationTasterScore();
        this.mutationTasterPred = dto.getMutationTasterPred();
        this.mutationAssessorScore = dto.getMutationAssessorScore();
        this.mutationAssessorPred = dto.getMutationAssessorPred();
        this.fathmmScore = dto.getFathmmScore();
        this.fathmmPred = dto.getFathmmPred();
        this.radialSVMScore = dto.getRadialSVMScore();
        this.radialSVMPred = dto.getRadialSVMPred();
        this.lrScore = dto.getLrScore();
        this.lrPred = dto.getLrPred();
        this.vest3Score = dto.getVest3Score();
        this.caddRaw = dto.getCaddRaw();
        this.caddPhred = dto.getCaddPhred();
        this.gerpRs = dto.getGerpRs();
        this.phyloP46wayPlacental = dto.getPhyloP46wayPlacental();
        this.phyloP100wayVertebrate = dto.getPhyloP100wayVertebrate();
        this.siphy29wayLogOdds = dto.getSiphy29wayLogOdds();
        this.exac03 = dto.getExac03();
        this.clinvar20150629 = dto.getClinvar20150629();
    }

    public Integer getId() {
        return id;
    }

    public Integer getVariantId() {
        return variantId;
    }

    public String getGenomicSuperDups() {
        return genomicSuperDups;
    }

    public Double getEsp6500siv2All() {
        return esp6500siv2All;
    }

    public Double getOctAll1000g2014() {
        return octAll1000g2014;
    }

    public Double getOctAfr1000g2014() {
        return octAfr1000g2014;
    }

    public Double getOctEas1000g2014() {
        return octEas1000g2014;
    }

    public Double getOctEur1000g2014() {
        return octEur1000g2014;
    }

    public String getSnp138() {
        return snp138;
    }

    public Double getSiftScore() {
        return siftScore;
    }

    public String getSiftPred() {
        return siftPred;
    }

    public Double getPolyphen2hdivScore() {
        return polyphen2hdivScore;
    }

    public String getPolyphen2hdivPred() {
        return polyphen2hdivPred;
    }

    public Double getPolyphen2hvarScore() {
        return polyphen2hvarScore;
    }

    public String getPolyphen2hvarPred() {
        return polyphen2hvarPred;
    }

    public Double getLrtScore() {
        return lrtScore;
    }

    public String getLrtPred() {
        return lrtPred;
    }

    public Double getMutationTasterScore() {
        return mutationTasterScore;
    }

    public String getMutationTasterPred() {
        return mutationTasterPred;
    }

    public Double getMutationAssessorScore() {
        return mutationAssessorScore;
    }

    public String getMutationAssessorPred() {
        return mutationAssessorPred;
    }

    public Double getFathmmScore() {
        return fathmmScore;
    }

    public String getFathmmPred() {
        return fathmmPred;
    }

    public Double getRadialSVMScore() {
        return radialSVMScore;
    }

    public String getRadialSVMPred() {
        return radialSVMPred;
    }

    public Double getLrScore() {
        return lrScore;
    }

    public String getLrPred() {
        return lrPred;
    }

    public Double getVest3Score() {
        return vest3Score;
    }

    public Double getCaddRaw() {
        return caddRaw;
    }

    public Double getCaddPhred() {
        return caddPhred;
    }

    public Double getGerpRs() {
        return gerpRs;
    }

    public Double getPhyloP46wayPlacental() {
        return phyloP46wayPlacental;
    }

    public Double getPhyloP100wayVertebrate() {
        return phyloP100wayVertebrate;
    }

    public Double getSiphy29wayLogOdds() {
        return siphy29wayLogOdds;
    }

    public Double getExac03() {
        return exac03;
    }

    public String getClinvar20150629() {
        return clinvar20150629;
    }

    @Override
    public String toString() {
        return "Annovar [id=" + id + "]";
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
        Annovar other = ( Annovar ) obj;
        if ( id == null ) {
            if ( other.id != null ) return false;
        } else if ( !id.equals( other.id ) ) return false;
        return true;
    }

}
