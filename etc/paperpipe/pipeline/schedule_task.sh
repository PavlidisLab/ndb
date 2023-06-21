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
LUIGI_ARGS="--worker-count-last-scheduled ${@:3}"

PAPERNAME=$(echo $BOOK | rev | cut -f1 -d"/" | cut -d"." -f2 | rev )

echo "Loading book: $BOOK Job: $JOB Extra args: $LUIGI_ARGS"

PYTHONPATH="flows:../scripts/models" exec luigi --module tasks "$JOB" --book "$BOOK" $LUIGI_ARGS
