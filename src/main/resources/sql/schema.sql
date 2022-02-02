-- MySQL dump 10.13  Distrib 8.0.27, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: marvdb_staging
-- ------------------------------------------------------
-- Server version	5.7.35-38-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `annovar_scores`
--

DROP TABLE IF EXISTS `annovar_scores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `annovar_scores` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `variant_id` int(11) NOT NULL,
  `genomicSuperDups` varchar(255) DEFAULT NULL,
  `esp6500siv2_all` float DEFAULT NULL,
  `1000g2014oct_all` float DEFAULT NULL,
  `1000g2014oct_afr` float DEFAULT NULL,
  `1000g2014oct_eas` float DEFAULT NULL,
  `1000g2014oct_eur` float DEFAULT NULL,
  `snp138` varchar(255) DEFAULT NULL,
  `SIFT_score` float DEFAULT NULL,
  `SIFT_pred` varchar(255) DEFAULT NULL,
  `Polyphen2_HDIV_score` float DEFAULT NULL,
  `Polyphen2_HDIV_pred` varchar(255) DEFAULT NULL,
  `Polyphen2_HVAR_score` float DEFAULT NULL,
  `Polyphen2_HVAR_pred` varchar(255) DEFAULT NULL,
  `LRT_score` float DEFAULT NULL,
  `LRT_pred` varchar(255) DEFAULT NULL,
  `MutationTaster_score` float DEFAULT NULL,
  `MutationTaster_pred` varchar(255) DEFAULT NULL,
  `MutationAssessor_score` float DEFAULT NULL,
  `MutationAssessor_pred` varchar(255) DEFAULT NULL,
  `FATHMM_score` float DEFAULT NULL,
  `FATHMM_pred` varchar(255) DEFAULT NULL,
  `RadialSVM_score` float DEFAULT NULL,
  `RadialSVM_pred` varchar(255) DEFAULT NULL,
  `LR_score` float DEFAULT NULL,
  `LR_pred` varchar(255) DEFAULT NULL,
  `VEST3_score` float DEFAULT NULL,
  `CADD_raw` float DEFAULT NULL,
  `CADD_phred` float DEFAULT NULL,
  `GERP_RS` float DEFAULT NULL,
  `phyloP46way_placental` float DEFAULT NULL,
  `phyloP100way_vertebrate` float DEFAULT NULL,
  `SiPhy_29way_logOdds` float DEFAULT NULL,
  `exac03` double DEFAULT NULL,
  `clinvar_20150629` mediumtext,
  `CADD13_raw` float DEFAULT NULL,
  `CADD13_phred` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `variant_id` (`variant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=670379 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exonic_func`
--

DROP TABLE IF EXISTS `exonic_func`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exonic_func` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `aa_change` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gene`
--

DROP TABLE IF EXISTS `gene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gene` (
  `gene_id` int(11) NOT NULL,
  `symbol` varchar(255) NOT NULL,
  `locus_tag` varchar(255) DEFAULT NULL,
  `synonyms` varchar(255) NOT NULL DEFAULT '',
  `xrefs` varchar(255) NOT NULL DEFAULT '',
  `chromosome` varchar(255) NOT NULL,
  `map_location` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `type_of_gene` varchar(255) NOT NULL DEFAULT '',
  `Symbol_from_nomenclature_authority` varchar(255) NOT NULL DEFAULT '',
  `Full_name_from_nomenclature_authority` varchar(255) NOT NULL DEFAULT '',
  `nomenclature_status` varchar(255) DEFAULT NULL,
  `other_designations` varchar(255) DEFAULT NULL,
  `modification_date` varchar(255) DEFAULT NULL,
  `feature_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`gene_id`),
  KEY `index_symbol` (`symbol`),
  KEY `index_chromosome` (`chromosome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gene_ranks`
--

DROP TABLE IF EXISTS `gene_ranks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gene_ranks` (
  `symbol` varchar(255) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `collection` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `papers`
--

DROP TABLE IF EXISTS `papers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `papers` (
  `id` int(11) NOT NULL DEFAULT '0',
  `url` mediumtext,
  `author` mediumtext NOT NULL,
  `paper_table` mediumtext,
  `technology` varchar(255) DEFAULT NULL,
  `parents` tinyint(1) DEFAULT NULL,
  `cohort` varchar(255) DEFAULT NULL,
  `cohort_source` varchar(255) DEFAULT NULL,
  `cohort_size` int(11) DEFAULT NULL,
  `doi` varchar(255) DEFAULT NULL,
  `title` longtext,
  `paper_key` varchar(255) DEFAULT NULL,
  `author_name` varchar(255) DEFAULT NULL,
  `publisher` longtext,
  `year` smallint(6) DEFAULT NULL,
  `cases` varchar(255) DEFAULT NULL,
  `count` longtext,
  `design` varchar(255) DEFAULT NULL,
  `pubmed_id` varchar(32) DEFAULT NULL,
  `pubmed_url` varchar(255) DEFAULT NULL,
  `summary` longtext,
  `scope` varchar(255) DEFAULT NULL,
  `reported_effects` varchar(255) DEFAULT NULL,
  `mut_reporting` varchar(255) DEFAULT NULL,
  `curation_notes` longtext,
  `display_count` int(11) DEFAULT NULL,
  `ambiguous_subjects` bit(1) DEFAULT NULL,
  `display_sequencing` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `raw_key_value`
--

DROP TABLE IF EXISTS `raw_key_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `raw_key_value` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `paper_id` int(11) NOT NULL,
  `raw_id` int(11) NOT NULL,
  `key` varchar(255) NOT NULL,
  `value` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `gene` (`paper_id`,`raw_id`,`key`),
  KEY `update_key` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=340314379 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `raw_variant`
--

DROP TABLE IF EXISTS `raw_variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `raw_variant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `paper_id` int(11) NOT NULL,
  `raw_id` int(11) NOT NULL,
  `sample_id` varchar(255) DEFAULT NULL,
  `chromosome` varchar(255) DEFAULT NULL,
  `start_hg18` int(11) DEFAULT NULL,
  `stop_hg18` int(11) DEFAULT NULL,
  `start_hg19` int(11) DEFAULT NULL,
  `stop_hg19` int(11) DEFAULT NULL,
  `ref` varchar(255) DEFAULT NULL,
  `alt` varchar(255) DEFAULT NULL,
  `gene` varchar(255) DEFAULT NULL,
  `effect` varchar(255) DEFAULT NULL,
  `code_change` varchar(255) DEFAULT NULL,
  `protein_change` varchar(255) DEFAULT NULL,
  `aa_change` longtext,
  `variant` varchar(255) DEFAULT NULL,
  `strand` varchar(255) DEFAULT NULL,
  `lof` varchar(255) DEFAULT NULL,
  `inheritance` varchar(32) DEFAULT NULL,
  `validation_method` varchar(255) DEFAULT NULL,
  `validation_reported` varchar(255) DEFAULT NULL,
  `validation` varchar(255) DEFAULT NULL,
  `phenotype` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `paper_id` (`paper_id`,`raw_id`,`sample_id`,`chromosome`)
) ENGINE=InnoDB AUTO_INCREMENT=1274517 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `variant`
--

DROP TABLE IF EXISTS `variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `variant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `paper_id` int(11) NOT NULL,
  `raw_variant_id` int(11) NOT NULL,
  `event_id` int(11) DEFAULT NULL,
  `subject_id` int(11) NOT NULL,
  `sample_id` varchar(255) DEFAULT NULL,
  `chromosome` varchar(255) DEFAULT NULL,
  `start_hg18` int(11) DEFAULT NULL,
  `start_hg19` int(11) DEFAULT NULL,
  `stop_hg19` int(11) DEFAULT NULL,
  `ref` varchar(255) DEFAULT NULL,
  `alt` varchar(255) DEFAULT NULL,
  `gene` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `code_change` varchar(255) DEFAULT NULL,
  `protein_change` varchar(255) DEFAULT NULL,
  `good_mutation` bit(1) NOT NULL,
  `gene_detail` mediumtext,
  `func` varchar(255) DEFAULT NULL,
  `aa_change` mediumtext,
  `cytoband` varchar(255) DEFAULT NULL,
  `lof` varchar(255) DEFAULT NULL,
  `inheritance` varchar(32) DEFAULT NULL,
  `validation_method` varchar(255) DEFAULT NULL,
  `validation_reported` varchar(255) DEFAULT NULL,
  `validation` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `paper_id` (`paper_id`,`sample_id`,`chromosome`,`raw_variant_id`),
  KEY `idx_chr_start_stop` (`chromosome`,`start_hg19`,`stop_hg19`),
  KEY `event_idx` (`event_id`),
  KEY `subject_idx` (`subject_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1512484 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `variant_func`
--

DROP TABLE IF EXISTS `variant_func`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `variant_func` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `func` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `variant_gene`
--

DROP TABLE IF EXISTS `variant_gene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `variant_gene` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `variant_id` int(11) NOT NULL,
  `gene_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_anno_gene` (`variant_id`,`gene_id`)
) ENGINE=InnoDB AUTO_INCREMENT=697634 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-02-02 11:52:04
