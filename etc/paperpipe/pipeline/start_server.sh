#!/bin/bash
set -eu

PIPELINE_PORT=16901
LOGDIR=logs/$(whoami)

mkdir -p "${LOGDIR}"

# Start the lugdi daemon.
luigid --background --pidfile PIDFILE --logdir "${LOGDIR}" --state-path STATE --port $PIPELINE_PORT

# Disable the db.config symlink.
ln -s -f /dev/null db.config

# Get rid of symlink if stale/still exists..
touch commits
rm commits
