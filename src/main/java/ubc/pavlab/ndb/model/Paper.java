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

import ubc.pavlab.ndb.model.dto.PaperDTO;

/**
 * Represents a Paper. Thread-safe.
 * 
 * @author mbelmadani
 * @version $Id$
 */
public final class Paper {
    private final Integer id;
    private final String url;
    private final String author;
    private final String paperTable;
    private final String mutReporting;
    private final String scope;
    private final boolean parents;
    private final String cohort;
    private final String cohortSource;
    private final Integer cohortSize;
    private final String reportedEffects;

    public Paper( PaperDTO dto ) {
        this.id = dto.getId();
        this.url = dto.getUrl();
        this.author = dto.getAuthor();
        this.paperTable = dto.getPaper_table();
        this.mutReporting = dto.getMut_reporting();
        this.scope = dto.getScope();
        this.parents = dto.isParents();
        this.cohort = dto.getCohort();
        this.cohortSource = dto.getCohort_source();
        this.cohortSize = dto.getCohort_size();
        this.reportedEffects = dto.getReported_effects();

    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthor() {
        return author;
    }

    public String getPaperTable() {
        return paperTable;
    }

    public String getMutReporting() {
        return mutReporting;
    }

    public String getScope() {
        return scope;
    }

    public boolean isParents() {
        return parents;
    }

    public String getCohort() {
        return cohort;
    }

    public String getCohortSource() {
        return cohortSource;
    }

    public Integer getCohortSize() {
        return cohortSize;
    }

    public String getReportedEffects() {
        return reportedEffects;
    }

    @Override
    public String toString() {
        return "Paper [id=" + this.id + ", author=" + this.author + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( this.id == null ) ? 0 : this.id.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Paper other = ( Paper ) obj;
        if ( id == null ) {
            if ( other.id != null ) return false;
        } else if ( !id.equals( other.id ) ) return false;
        return true;
    }

}