#!/bin/bash
set -eu

if [ "$#" -eq "0" ]; then
    echo "Description:"
    echo "Sets up the db.config and commits directory to point to a specific database."
    echo "Usage:"
    echo "$0 <DATABASE_TYPE>"
    echo "Example:"
    echo "$0 staging"
    echo "Result:"
    echo "db.config and the commits directory should be updated to point to the correct sources."

    exit 1
fi

DB="$1"

# Remove symlink
rm commits
rm db.config

# Make new symlinks
ln -s -f "db.config.""$DB" "db.config"
ln -s -f "commits-""$DB" "commits"
