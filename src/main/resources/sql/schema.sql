CREATE TABLE `annovar_scores`
(
    `id`                      int NOT NULL AUTO_INCREMENT,
    `variant_id`              int NOT NULL,
    `genomicSuperDups`        varchar(255),
    `esp6500siv2_all`         float,
    `1000g2014oct_all`        float,
    `1000g2014oct_afr`        float,
    `1000g2014oct_eas`        float,
    `1000g2014oct_eur`        float,
    `snp138`                  varchar(255),
    `SIFT_score`              float,
    `SIFT_pred`               varchar(255),
    `Polyphen2_HDIV_score`    float,
    `Polyphen2_HDIV_pred`     varchar(255),
    `Polyphen2_HVAR_score`    float,
    `Polyphen2_HVAR_pred`     varchar(255),
    `LRT_score`               float,
    `LRT_pred`                varchar(255),
    `MutationTaster_score`    float,
    `MutationTaster_pred`     varchar(255),
    `MutationAssessor_score`  float,
    `MutationAssessor_pred`   varchar(255),
    `FATHMM_score`            float,
    `FATHMM_pred`             varchar(255),
    `RadialSVM_score`         float,
    `RadialSVM_pred`          varchar(255),
    `LR_score`                float,
    `LR_pred`                 varchar(255),
    `VEST3_score`             float,
    `CADD_raw`                float,
    `CADD_phred`              float,
    `GERP_RS`                 float,
    `phyloP46way_placental`   float,
    `phyloP100way_vertebrate` float,
    `SiPhy_29way_logOdds`     float,
    `exac03`                  double,
    `clinvar_20150629`        mediumtext,
    `CADD13_raw`              float,
    `CADD13_phred`            float,
    PRIMARY KEY (`id`),
    UNIQUE KEY `variant_id` (`variant_id`)
);

CREATE TABLE `exonic_func`
(
    `id`        int NOT NULL AUTO_INCREMENT,
    `category`  varchar(255),
    `aa_change` mediumtext,
    PRIMARY KEY (`id`)
);

CREATE TABLE `gene`
(
    `gene_id`                               int          NOT NULL,
    `symbol`                                varchar(255) NOT NULL,
    `locus_tag`                             varchar(255),
    `synonyms`                              varchar(255) NOT NULL DEFAULT '',
    `xrefs`                                 varchar(255) NOT NULL DEFAULT '',
    `chromosome`                            varchar(255) NOT NULL,
    `map_location`                          varchar(255) NOT NULL DEFAULT '',
    `description`                           varchar(255) NOT NULL DEFAULT '',
    `type_of_gene`                          varchar(255) NOT NULL DEFAULT '',
    `Symbol_from_nomenclature_authority`    varchar(255) NOT NULL DEFAULT '',
    `Full_name_from_nomenclature_authority` varchar(255) NOT NULL DEFAULT '',
    `nomenclature_status`                   varchar(255),
    `other_designations`                    varchar(255),
    `modification_date`                     varchar(255),
    `feature_type`                          varchar(255),
    PRIMARY KEY (`gene_id`),
    KEY `index_symbol` (`symbol`),
    KEY `index_chromosome` (`chromosome`)
);

CREATE TABLE `gene_ranks`
(
    `symbol`     varchar(255),
    `score`      int,
    `collection` varchar(255)
);

CREATE TABLE `papers`
(
    `id`                 int        NOT NULL DEFAULT '0',
    `url`                mediumtext,
    `author`             mediumtext NOT NULL,
    `paper_table`        mediumtext,
    `technology`         varchar(255),
    `parents`            tinyint(1),
    `cohort`             varchar(255),
    `cohort_source`      varchar(255),
    `cohort_size`        int,
    `doi`                varchar(255),
    `title`              longtext,
    `paper_key`          varchar(255),
    `author_name`        varchar(255),
    `publisher`          longtext,
    `year`               smallint(6),
    `cases`              varchar(255),
    `count`              longtext,
    `design`             varchar(255),
    `pubmed_id`          varchar(32),
    `pubmed_url`         varchar(255),
    `summary`            longtext,
    `scope`              varchar(255),
    `reported_effects`   varchar(255),
    `mut_reporting`      varchar(255),
    `curation_notes`     longtext,
    `display_count`      int,
    `ambiguous_subjects` bit(1),
    `display_sequencing` varchar(255),
    `created_at`         timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `raw_key_value`
(
    `id`       int          NOT NULL AUTO_INCREMENT,
    `paper_id` int          NOT NULL,
    `raw_id`   int          NOT NULL,
    `key`      varchar(255) NOT NULL,
    `value`    longtext,
    PRIMARY KEY (`id`),
    UNIQUE KEY `gene` (`paper_id`, `raw_id`, `key`),
    KEY `update_key` (`key`)
);

CREATE TABLE `raw_variant`
(
    `id`                  int NOT NULL AUTO_INCREMENT,
    `paper_id`            int NOT NULL,
    `raw_id`              int NOT NULL,
    `sample_id`           varchar(255),
    `chromosome`          varchar(255),
    `start_hg18`          int,
    `stop_hg18`           int,
    `start_hg19`          int,
    `stop_hg19`           int,
    `ref`                 varchar(255),
    `alt`                 varchar(255),
    `gene`                varchar(255),
    `effect`              varchar(255),
    `code_change`         varchar(255),
    `protein_change`      varchar(255),
    `aa_change`           longtext,
    `variant`             varchar(255),
    `strand`              varchar(255),
    `lof`                 varchar(255),
    `inheritance`         varchar(32),
    `validation_method`   varchar(255),
    `validation_reported` varchar(255),
    `validation`          varchar(255),
    `phenotype`           varchar(255),
    PRIMARY KEY (`id`),
    KEY `paper_id` (`paper_id`, `raw_id`, `sample_id`, `chromosome`)
);

CREATE TABLE `variant`
(
    `id`                  int    NOT NULL AUTO_INCREMENT,
    `paper_id`            int    NOT NULL,
    `raw_variant_id`      int    NOT NULL,
    `event_id`            int,
    `subject_id`          int    NOT NULL,
    `sample_id`           varchar(255),
    `chromosome`          varchar(255),
    `start_hg18`          int,
    `start_hg19`          int,
    `stop_hg19`           int,
    `ref`                 varchar(255),
    `alt`                 varchar(255),
    `gene`                varchar(255),
    `category`            varchar(255),
    `code_change`         varchar(255),
    `protein_change`      varchar(255),
    `good_mutation`       bit(1) NOT NULL,
    `gene_detail`         mediumtext,
    `func`                varchar(255),
    `aa_change`           mediumtext,
    `cytoband`            varchar(255),
    `lof`                 varchar(255),
    `inheritance`         varchar(32),
    `validation_method`   varchar(255),
    `validation_reported` varchar(255),
    `validation`          varchar(255),
    PRIMARY KEY (`id`),
    KEY `paper_id` (`paper_id`, `sample_id`, `chromosome`, `raw_variant_id`),
    KEY `idx_chr_start_stop` (`chromosome`, `start_hg19`, `stop_hg19`),
    KEY `event_idx` (`event_id`),
    KEY `subject_idx` (`subject_id`)
);

CREATE TABLE `variant_func`
(
    `id`   int NOT NULL AUTO_INCREMENT,
    `func` varchar(255),
    PRIMARY KEY (`id`)
);

CREATE TABLE `variant_gene`
(
    `id`         int NOT NULL AUTO_INCREMENT,
    `variant_id` int NOT NULL,
    `gene_id`    int NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_anno_gene` (`variant_id`, `gene_id`)
);