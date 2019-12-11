#!/bin/bash
set -eu

PAPER_ID=${1}

echo " [INFO] Checking for LoadPaper commit file:"
set +eu
grep -F "[${PAPER_ID}," commits/paper* | cut -f1 -d":" | sort | uniq 
set -eu

if [[ "${?}" == "1" ]]; then
    echo "[INFO] No commit files founds."
fi

echo
echo " [INFO] Checking for downstream task commits files: "
set +eu
ls "commits/"*"paper${PAPER_ID}.out" 2> /dev/null
RETCODE=$?
set -eu

if [[ $RETCODE == "2" ]]; then
    echo " [INFO] No commit files founds."
fi
