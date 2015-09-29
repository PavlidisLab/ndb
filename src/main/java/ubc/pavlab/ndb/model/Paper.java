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
 * Represents a Paper. Thread-safe.
 * 
 * @author mbelmadani
 * @version $Id$
 */
public final class Paper {
    private final Integer id;
    private final String author;
    private final String paper_table;
    private final String mut_reporting;
    private final String scope;
    private final boolean parents;
    private final String cohort;
    private final String cohort_source;
    private final Integer cohort_size;
    private final String reported_effects;

    private Paper( PaperBuilder builder ) {
        this.id = builder.id;
        this.author = builder.author;
        this.paper_table = builder.paper_table;
        this.mut_reporting = builder.mut_reporting;
        this.scope = builder.scope;
        this.parents = builder.parents;
        this.cohort = builder.cohort;
        this.cohort_source = builder.cohort_source;
        this.cohort_size = builder.cohort_size;
        this.reported_effects = builder.reported_effects;
    }

    public Integer getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getPaper_table() {
        return paper_table;
    }

    public String getMut_reporting() {
        return mut_reporting;
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

    public String getCohort_source() {
        return cohort_source;
    }

    public Integer getCohort_size() {
        return cohort_size;
    }

    public String getReported_effects() {
        return reported_effects;
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

    public static class PaperBuilder {
        private final Integer id;
        private final String author;
        private final String paper_table;
        private final String mut_reporting;
        private final String scope;
        private final boolean parents;
        private final String cohort;
        private final String cohort_source;
        private final Integer cohort_size;
        private final String reported_effects;

        public PaperBuilder( Integer id, String author, String paper_table, String mut_reporting, String scope,
                boolean parents, String cohort, String cohort_source, Integer cohort_size, String reported_effects ) {
            super();
            this.id = id;
            this.author = author;
            this.paper_table = paper_table;
            this.mut_reporting = mut_reporting;
            this.scope = scope;
            this.parents = parents;
            this.cohort = cohort;
            this.cohort_source = cohort_source;
            this.cohort_size = cohort_size;
            this.reported_effects = reported_effects;
        }

        public Paper build() {
            return new Paper( this );
        }
    }

}
