#!/bin/bash

VCF=$1
MACHINE="frink.msl.ubc.ca"
echo "Using $MACHINE for processing."


WORK="/space/grp/VariCarta/curation/tools/annovar"
UNAME=$(whoami)

echo "Submitting input"
ssh $UNAME@$MACHINE <<EOF
rm $WORK/tmp/*
EOF

rsync -a flows/tmp/ $UNAME@$MACHINE:$WORK/tmp/

echo "Generating output"
ssh $UNAME@$MACHINE <<EOF
cd $WORK && python $WORK/annovar_vcf.py $WORK/tmp/ $WORK/tmp/
EOF

echo "Retreiving output"
rsync -a $UNAME@$MACHINE:$WORK/tmp/ flows/tmp/
