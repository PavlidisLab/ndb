#!/bin/bash
set -eu

##
## Load curation notes to the database.
##

INFILE=$1
PAPERID=$2
sqlUpdateCurationNotes(){
    ## Update the curation field in the papers table
    ## 
    set -eu
    source /tmp/db.config
    


    DATABASE=$db #"marvdb_staging"
    PAPERTABLE="papers"
   
    INFILE=$1
    PAPERID=$2

    QUERY=" USE $DATABASE; UPDATE $PAPERTABLE SET curation_notes=LOAD_FILE('$INFILE') WHERE id='$2'; "
    echo -e "[${FUNCNAME[0]}]: "$QUERY"...\c"
    echo $QUERY | mysql -h$host -u$dbuser -p$dbpass
    echo "OK."
    return 0
}
export -f sqlUpdateCurationNotes
export CWD=$PWD

PAPERROOT=$(dirname $(readlink -f $INFILE))
cp $PAPERROOT/NOTES /tmp/INFILE
sqlUpdateCurationNotes $PAPERROOT $PAPERID
