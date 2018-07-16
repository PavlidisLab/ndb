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

import ubc.pavlab.ndb.model.Gene;

/**
 * Data Transfer Object for {@link Gene}
 * 
 * @author mbelmadani
 * @version $Id$
 */
public final class PaperDTO {
    private final Integer id;
    private final String url;
    private final String key;
    private final String title;
    private final String year;
    private final String doi;
    private final String pubmed_id;
    private final String author;
    private final String paper_table;
    private final String technology;
    private final String cohort_source;
    private final String count;
    private final String cases;
    private final String design;
    private final String publisher;
    private final Integer display_count;
    private final boolean ambiguous_subjects;

    public PaperDTO( Integer id, String url, String title, String key, String year, String doi, String pubmed_id, String author,
            String paper_table, String technology, String cohort_source,  String count, String cases,
            String design, String publisher, Integer display_count, boolean ambiguous_subjects ) {
        super();
        this.id = id;
        this.url = url;
        this.key = key;
        this.title = title;
        this.year = year;
        this.doi = doi;
        this.pubmed_id = pubmed_id;
        this.author = author;
        this.paper_table = paper_table;
        this.technology = technology;
        this.cohort_source = cohort_source;
        this.count = count;
        this.cases = cases;
        this.design = design;
        this.publisher = publisher;
        this.display_count = display_count;
        this.ambiguous_subjects = ambiguous_subjects;
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

    public String getPaper_table() {
        return paper_table;
    }

    public String getCohort_source() {
        return cohort_source;
    }

    public String getCount() {
        return count;
    }

    public String getDOI() {
        return doi;
    }

    public String getKey() {
        return key;
    }

    public String getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    public String getCases() {
        return cases;
    }

    public String getDesign() {
        return design;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPubmed_id() {
        return pubmed_id;
    }

    public String getTechnology() {
        return technology;
    }

    public Integer getDisplay_count() {
        return display_count;
    }

    public boolean getAmbiguousSubjects() {
        return ambiguous_subjects;
    }


}
