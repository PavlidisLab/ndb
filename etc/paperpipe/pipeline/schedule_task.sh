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

INTERACTIVE=0
if [ "$#" -gt "2" ]
  then
    if [ "$3" == "--interactive" ]
    then
	INTERACTIVE=1
    fi
fi

BOOK=$1
JOB=$2

PAPERNAME=$(echo $BOOK | rev | cut -f1 -d"/" | cut -d"." -f2 | rev )

echo "Loading book: $BOOK"
#PYTHONPATH='flows:../scripts/models' luigi --module main LoadPaper --book $BOOK
#PYTHONPATH='flows:../scripts/models' luigi --module main LoadRawKV --book $BOOK
#PYTHONPATH='flows:../scripts/models' luigi --module main LoadPaper --book $1 --local-scheduler



if [ $INTERACTIVE -eq 0  ];
then
    PYTHONPATH="flows:../scripts/models" luigi --module tasks $JOB --book $BOOK --worker-count-last-scheduled --scheduler-port 16901 > logs/$PAPERNAME.log 2> logs/$PAPERNAME.err
else
    PYTHONPATH="flows:../scripts/models" luigi --module tasks $JOB --book $BOOK --worker-count-last-scheduled --scheduler-port 16901
fi
