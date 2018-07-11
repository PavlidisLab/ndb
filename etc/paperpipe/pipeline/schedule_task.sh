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
    else
	echo "Error! Unknown switch $3"
	exit -1
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
    LOGLOG=logs/$PAPERNAME.log
    ERRLOG=logs/$PAPERNAME.err
    PYTHONPATH="flows:../scripts/models" luigi --module tasks $JOB --book $BOOK --worker-count-last-scheduled --scheduler-port 16901 > $LOGLOG 2> $ERRLOG
    echo "See logs at $LOGLOG / $ERRLOG"
else
    PYTHONPATH="flows:../scripts/models" luigi --module tasks $JOB --book $BOOK --worker-count-last-scheduled --scheduler-port 16901
fi
