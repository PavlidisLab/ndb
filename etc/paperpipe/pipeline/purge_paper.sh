#!/bin/bash
set -eu

trap 'echo Error at line $LINENO $(basename "$0")' ERR

TASK="all"
PAPERID="UNDEFINED"

if [[ $# -lt 1 ]]; then
    echo "Usage:"
    echo "$0 PAPERID (Optional, TASK)"
    echo "TASK can be all, annovar, variant, rawvariant, rkv, or paper"
    exit -1
fi

PAPERID=$1
if [ "$#" -eq 2 ]; then TASK=$2; fi
if [ "$#" -gt 2 ]; then echo "Too many arguments." ; $0 ; exit -1 ; fi
echo "Preparing to delete $TASK from paper# $PAPERID";

DELETE_ANNOVAR=" DELETE FROM annovar_scores where variant_id in (SELECT id FROM variant WHERE paper_id=$PAPERID); "

DELETE_ANNOVAR_CATEGORY=" UPDATE variant SET category = NULL WHERE paper_id=$PAPERID; "
DELETE_ANNOVAR_AACHANGE=" UPDATE variant SET aa_change = NULL  WHERE paper_id=$PAPERID; "
DELETE_ANNOVAR_FUNC=" UPDATE variant SET func = NULL  WHERE paper_id=$PAPERID; "
DELETE_ANNOVAR_GENE=" DELETE FROM variant_gene WHERE variant_id in (SELECT id FROM variant WHERE paper_id=$PAPERID); "

DELETE_VARIANT=" DELETE FROM variant where paper_id=$PAPERID; "
DELETE_RAWVARIANT=" DELETE FROM raw_variant where paper_id=$PAPERID; "
DELETE_RAWKV=" DELETE FROM raw_key_value where paper_id=$PAPERID; "
DELETE_PAPER=" DELETE FROM papers where id=$PAPERID; "

source db.config
DATABASE_IN=" mysql -u$dbuser -p$dbpass -h$host "
DATABASE_NAME=" $db "

PAPERMARKER="[$PAPERID," # Not a fan of this, but here because in the commit files, the first element in the [] list is the paper ID, so hence the [.
export PAPERMARKER

if [[ "$TASK" == "all" ]]; then

    echo "DELETING ALL COMMITS."
    echo "use $DATABASE_NAME; $DELETE_ANNOVAR ;  $DELETE_ANNOVAR_CATEGORY; $DELETE_ANNOVAR_FUNC; $DELETE_ANNOVAR_GENE; $DELETE_ANNOVAR_AACHANGE; $DELETE_VARIANT $DELETE_RAWVARIANT $DELETE_RAWKV $DELETE_PAPER " | $DATABASE_IN

    RELCOMMITS=$(find commits/ -name "*_paper$PAPERID.out" | wc -l)
    echo "Number of relevant commits found:" $RELCOMMITS
    if [ "$RELCOMMITS" -eq "0" ]; then
	echo "Exiting."
	exit	   
    fi

    # Delete all other commit files
    echo "Files to delete (excluding the paper commit):"
    echo "Total:" $(find commits/ -name "*_paper$PAPERID.out" | wc -l)
    find commits/ -name "*_paper$PAPERID.out"
    find commits/ -name "*_paper$PAPERID.out" -delete

    # Delete paper commit file
    paperCommit=$(grep -F $PAPERMARKER commits/paper* /dev/null | cut -f1 -d":")
    echo "Removing $paperCommit"
    rm $paperCommit

    echo "Commit files for paper $PAPERID deleted"

fi

if [[ "$TASK" == "annovar" ]]; then
    echo "use $DATABASE_NAME; $DELETE_ANNOVAR; $DELETE_ANNOVAR_CATEGORY; $DELETE_ANNOVAR_FUNC; $DELETE_ANNOVAR_GENE; $DELETE_ANNOVAR_AACHANGE; " | $DATABASE_IN
    echo "Task is $TASK"
    rm commits/$TASK"_paper"$PAPERID.out
    echo "'$TASK' commit file deleted."       
fi

if [[ "$TASK" == "variant" ]]; then    
    echo "use $DATABASE_NAME;  $DELETE_VARIANT " | $DATABASE_IN 
    echo ";variant; deleted for paper $PAPERID"
    rm commits/$TASK"_paper"$PAPERID.out
    echo "'$TASK' commit file deleted."   
fi

if [[ "$TASK" == "rawvariant" ]]; then
    echo "use $DATABASE_NAME;  $DELETE_RAWVARIANT " | $DATABASE_IN 
    echo ";raw_variant; deleted for paper $PAPERID"
    rm commits/$TASK"_paper"$PAPERID.out
    echo "'$TASK' commit file deleted."   
fi

if [[ "$TASK" == "rkv" ]]; then
    echo "use $DATABASE_NAME; $DELETE_RAWKV  " | $DATABASE_IN 
    echo ";raw_key_value; deleted for paper $PAPERID"
    rm commits/$TASK"_paper"$PAPERID.out
    echo "'$TASK' commit file deleted."   
fi

if [[ "$TASK" == "paper" ]]; then
    echo "use $DATABASE_NAME; $DELETE_PAPER " | $DATABASE_IN
    echo ";papers; deleted for paper $PAPERID"
    
    # Delete paper
    paperCommit=$(grep -F $PAPERMARKER commits/paper* /dev/null | cut -f1 -d":")
    rm $paperCommit
    echo "'$TASK' commit file deleted."   
fi

