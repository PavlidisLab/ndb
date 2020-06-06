#!/bin/bash
set -eu

if [ $# -lt 2 ]; then
    echo "Usage:"
    echo $0 USERNAME DATABASE
    exit
fi

PASSWORD=""
if [ $# -eq 3 ]; then
    PASSWORD=$3
fi

DUMP_ROOT="../out/"
DBUSER="$1"
DATABASE="$2"
HOST="127.0.0.1"

QUERY=\
"use $DATABASE; 
SELECT 
    pid,
    paper_key,
    v.id,
    paper_id,
    sequencing_study_type,
    event_id,
    subject_id,
    sample_id,
    chromosome,
    start_hg19,
    stop_hg19,
    ref,
    alt,
    symbol as gene_symbol,
    category,
    code_change,
    protein_change,
    gene_detail,
    func,
    aa_change,
    cytoband,
    inheritance,
    validation_method,
    validation_reported,
    validation,
    exac03,
    clinvar_20150629,
    CADD13_raw,
    CADD13_phred,
    SIFT_score,
    SIFT_pred,
    Polyphen2_HDIV_score,
    Polyphen2_HDIV_pred,
    Polyphen2_HVAR_score,
    Polyphen2_HVAR_pred,
    LRT_score,
    LRT_pred,
    MutationTaster_score,
    MutationTaster_pred,
    MutationAssessor_score,
    MutationAssessor_pred,
    FATHMM_score,
    FATHMM_pred,
    RadialSVM_score,
    RadialSVM_pred,
    LR_score,
    LR_pred,
    VEST3_score,
    CADD_raw,
    CADD_phred,
    GERP_RS,
    phyloP46way_placental,
    phyloP100way_vertebrate,
    SiPhy_29way_logOdds
FROM variant v
    LEFT JOIN annovar_scores on annovar_scores.variant_id = v.id 
    LEFT JOIN (SELECT id as pid, paper_key, display_sequencing as sequencing_study_type from papers) p ON p.pid = v.paper_id
    LEFT JOIN (SELECT gene_id, variant_id from variant_gene) vg ON vg.variant_id = v.id
    LEFT JOIN (SELECT symbol, gene_id from gene) g ON g.gene_id = vg.gene_id;"


TIMESTAMP=$(date +%Y-%m-%d_%H-%M-%S)

# Make sure the directory exists
mkdir -p "${DUMP_ROOT}"/"${DATABASE}" 

# Write current database
OUTDUMP="${DUMP_ROOT}"/"${DATABASE}"/varicarta_dump_"${TIMESTAMP}".tsv

# Pointer to latest dump
OUTLATEST="${DUMP_ROOT}"/export_latest.tsv

# Execute query to dump database
echo " $QUERY" | mysql -u$DBUSER -h$HOST -p$PASSWORD --default-character-set=utf8 -p > $OUTDUMP

echo "Wrote database to $OUTDUMP"
echo "Update pointer to 'latest' version?"
rm -i "${OUTLATEST}" # Prompt for overwrite if needed.
ln -s "${OUTDUMP}" "${OUTLATEST}"
