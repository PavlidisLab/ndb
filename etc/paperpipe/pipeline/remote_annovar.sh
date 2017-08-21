#!/bin/bash

VCF=$1
WORK="/space/grp/MARVdb/curation/tools/annovar"
UNAME="$USER" #   <-- CHANGE TO YOUR USERNAME, do not commit to git
"""
echo "Submitting input"
ssh $UNAME@troy <<EOF
rm $WORK/tmp/*
EOF

rsync -a flows/tmp/ $UNAME@troy:$WORK/tmp/

echo "Generating output"
ssh $UNAME@troy <<EOF
cd $WORK && python $WORK/annovar_vcf.py $WORK/tmp/ $WORK/tmp/
EOF
"""
echo "Retreiving output"
rsync -a $UNAME@troy:$WORK/tmp/ flows/tmp/
