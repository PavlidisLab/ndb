#!/bin/bash

VCF=$1
WORK="/home/mbelmadani/development/marvdb/annovar"
UNAME="change_me" #   <-- CHANGE TO YOUR USERNAME, do not commit to git

echo "Submitting input"
ssh $UNAME@troy <<EOF
rm $WORK/tmp/*
EOF

rsync -a flows/tmp/ $UNAME@troy:/home/mbelmadani/development/marvdb/annovar/tmp/

echo "Generating output"
ssh $UNAME@troy <<EOF
cd $WORK && python $WORK/annovar_vcf.py $WORK/tmp/
EOF

echo "Retreiving output"
rsync -a $UNAME@troy:/home/mbelmadani/development/marvdb/annovar/tmp/ flows/tmp/
