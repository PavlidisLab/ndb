#!/bin/bash
#
# Submit a task to the scheduler
#
set -eu

BOOK=$1
JOB=$2

if [ $# -eq 0 ]
  then
    BOOK="../exampledata/dyrk1a/variants.xlsx"
    JOB=LoadVariant
    echo "No arguments supplied. Using template at $BOOK"
fi

echo "Loading book: $BOOK"
#PYTHONPATH='flows:../scripts/models' luigi --module main LoadPaper --book $BOOK
#PYTHONPATH='flows:../scripts/models' luigi --module main LoadRawKV --book $BOOK
#PYTHONPATH='flows:../scripts/models' luigi --module main LoadPaper --book $1 --local-scheduler

PYTHONPATH='flows:../scripts/models' luigi --module tasks $JOB --book $BOOK
