#!/bin/bash
set -eu

## ssh to server
if [[ "${#}" -eq 0 ]]; then
    REMOTEUSER=$(whoami)
else
    REMOTEUSER="${1}"
fi

echo " Making tunnel to lenny/prod-db from port 3307 to 3306"
ssh -p22000 -f "${REMOTEUSER}"@lenny.msl.ubc.ca -L 3307:localhost:3306 -N

echo "Access database from 'localhost':"
echo "### mysql -undb -p -h 127.0.0.1 -P 3307"

