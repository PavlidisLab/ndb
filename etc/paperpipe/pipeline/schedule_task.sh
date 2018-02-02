#!/bin/bash
#
# Submit a task to the scheduler
#
set -eu

if [ "$#" -lt "2" ]
  then
    BOOK="../exampledata/dyrk1a/variants.xlsx"
    JOB=LoadVariant
    echo ""
    echo "Usage:"
    echo "$0 $BOOK $JOB"
    echo ""
    exit
fi

BOOK=$1
JOB=$2

echo "Loading book: $BOOK"
#PYTHONPATH='flows:../scripts/models' luigi --module main LoadPaper --book $BOOK
#PYTHONPATH='flows:../scripts/models' luigi --module main LoadRawKV --book $BOOK
#PYTHONPATH='flows:../scripts/models' luigi --module main LoadPaper --book $1 --local-scheduler

PYTHONPATH='flows:../scripts/models' luigi --module tasks $JOB --book $BOOK
