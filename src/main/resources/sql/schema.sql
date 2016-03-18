-- MySQL dump 10.13  Distrib 5.6.19, for debian-linux-gnu (x86_64)
--
-- Host: abe    Database: ndb
-- ------------------------------------------------------
-- Server version	5.6.23-72.1-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
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
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `annovar_scores` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `variant_id` int(11) NOT NULL,
  `genomicSuperDups` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `esp6500siv2_all` float DEFAULT NULL,
  `1000g2014oct_all` float DEFAULT NULL,
  `1000g2014oct_afr` float DEFAULT NULL,
  `1000g2014oct_eas` float DEFAULT NULL,
  `1000g2014oct_eur` float DEFAULT NULL,
  `snp138` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SIFT_score` float DEFAULT NULL,
  `SIFT_pred` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `Polyphen2_HDIV_score` float DEFAULT NULL,
  `Polyphen2_HDIV_pred` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `Polyphen2_HVAR_score` float DEFAULT NULL,
  `Polyphen2_HVAR_pred` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `LRT_score` float DEFAULT NULL,
  `LRT_pred` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `MutationTaster_score` float DEFAULT NULL,
  `MutationTaster_pred` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `MutationAssessor_score` float DEFAULT NULL,
  `MutationAssessor_pred` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `FATHMM_score` float DEFAULT NULL,
  `FATHMM_pred` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `RadialSVM_score` float DEFAULT NULL,
  `RadialSVM_pred` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `LR_score` float DEFAULT NULL,
  `LR_pred` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VEST3_score` float DEFAULT NULL,
  `CADD_raw` float DEFAULT NULL,
  `CADD_phred` float DEFAULT NULL,
  `GERP_RS` float DEFAULT NULL,
  `phyloP46way_placental` float DEFAULT NULL,
  `phyloP100way_vertebrate` float DEFAULT NULL,
  `SiPhy_29way_logOdds` float DEFAULT NULL,
  `exac03` double DEFAULT NULL,
  `clinvar_20150629` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `variant_id` (`variant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5565 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gene`
--

DROP TABLE IF EXISTS `gene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gene` (
  `gene_id` int(11) NOT NULL,
  `symbol` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `synonyms` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `xrefs` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `chromosome` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `map_location` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `description` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `type_of_gene` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `Symbol_from_nomenclature_authority` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `Full_name_from_nomenclature_authority` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `modification_date` varchar(16) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`gene_id`),
  KEY `index_symbol` (`symbol`),
  KEY `index_chromosome` (`chromosome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `loss_of_function`
--

--
-- Table structure for table `papers`
--

DROP TABLE IF EXISTS `papers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `papers` (
  `id` int(11) NOT NULL DEFAULT '0',
  `url` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `author` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `paper_table` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `mut_reporting` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `scope` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `parents` tinyint(1) DEFAULT NULL,
  `cohort` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `cohort_source` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `cohort_size` int(11) DEFAULT NULL,
  `reported_effects` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `doi` varchar(255) DEFAULT NULL,
  `title` text,
  `paper_key` varchar(255) DEFAULT NULL,
  `author_name` varchar(255) DEFAULT NULL,
  `publisher` text,
  `year` smallint(6) DEFAULT NULL,
  `cases` varchar(255) DEFAULT NULL,
  `count` text,
  `design` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `raw_key_value`
--

DROP TABLE IF EXISTS `raw_key_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `raw_key_value` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `paper_id` int(11) NOT NULL,
  `raw_id` int(11) NOT NULL,
  `key` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `value` mediumtext COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `gene` (`paper_id`,`raw_id`,`key`)
) ENGINE=InnoDB AUTO_INCREMENT=233047 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `raw_variant`
--

DROP TABLE IF EXISTS `raw_variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `raw_variant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `paper_id` int(11) NOT NULL,
  `raw_id` int(11) NOT NULL,
  `sample_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `chromosome` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start_hg18` int(11) DEFAULT NULL,
  `stop_hg18` int(11) DEFAULT NULL,
  `start_hg19` int(11) DEFAULT NULL,
  `stop_hg19` int(11) DEFAULT NULL,
  `ref` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `alt` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `gene` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `effect` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `code_change` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `protein_change` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `aa_change` mediumtext COLLATE utf8_unicode_ci,
  `variant` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `strand` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `denovo` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `lof` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `paper_id` (`paper_id`,`raw_id`,`sample_id`,`chromosome`)
) ENGINE=InnoDB AUTO_INCREMENT=12278 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `variant`
--

DROP TABLE IF EXISTS `variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `variant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `paper_id` int(11) NOT NULL,
  `raw_variant_id` int(11) NOT NULL,
  `event_id` int(11) DEFAULT NULL,
  `subject_id` int(11) NOT NULL,
  `sample_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `chromosome` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start_hg18` int(11) DEFAULT NULL,
  `start_hg19` int(11) DEFAULT NULL,
  `stop_hg19` int(11) DEFAULT NULL,
  `ref` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `alt` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `gene` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `category` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `code_change` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `protein_change` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `good_mutation` bit(1) NOT NULL,
  `gene_detail` text COLLATE utf8_unicode_ci,
  `func` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `aa_change` text COLLATE utf8_unicode_ci,
  `cytoband` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `denovo` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `lof` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `paper_id` (`paper_id`,`sample_id`,`chromosome`,`raw_variant_id`),
  KEY `idx_chr_start_stop` (`chromosome`,`start_hg19`,`stop_hg19`),
  KEY `event_idx` (`event_id`),
  KEY `subject_idx` (`subject_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5585 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `variant_gene`
--

DROP TABLE IF EXISTS `variant_gene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `variant_gene` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `variant_id` int(11) NOT NULL,
  `gene_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_anno_gene` (`variant_id`,`gene_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8192 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-03-15 14:48:39
