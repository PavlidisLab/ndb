#!/bin/bash

VCF=$1
WORK="/home/mbelmadani/development/marvdb/annovar"

echo "Submitting input"
ssh mbelmadani@troy <<EOF 
rm $WORK/tmp/*
EOF

rsync -a flows/tmp/ mbelmadani@troy:/home/mbelmadani/development/marvdb/annovar/tmp/

echo "Generating output"
ssh mbelmadani@troy <<EOF 
cd $WORK && python $WORK/annovar_vcf.py $WORK/tmp/
EOF

echo "Retreiving output"
rsync -a mbelmadani@troy:/home/mbelmadani/development/marvdb/annovar/tmp/ flows/tmp/


