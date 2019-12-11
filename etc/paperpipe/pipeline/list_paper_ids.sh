#!/bin/bash
set -eu

if [[ $# -gt 1 ]]; then
    echo "Usage:"
    echo "$0 {1,2}"
    echo "1 will sort by paper ID, 2 will sort the author name."
    exit -1
fi

SORTBY=1
if [[ $# -eq 1 ]]; then
    SORTBY=$1
fi

find commits/ -type f -name "paper*" \
    | xargs -I@ bash -c 'head -n2 $1 | tail -n1 | tr -d "[" | cut -f1,2 -d, ' _ @ \;  \
    | sed "s|,.*'|\t|g" \
    | sort -k"${SORTBY}"g

echo
echo "[WARNING] Use with caution!" >&2
echo "Paper IDs and First author name reported. Please inspect the files manually before running a purge, as some of these matches may be duplicates if there's more than one file in the commit directory." >&2
echo "Do not blindly delete paper with matching author name as those might be used in multiple papers." >&2
