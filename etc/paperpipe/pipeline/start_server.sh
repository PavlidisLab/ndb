#!/bin/bash
set -eu

PIPELINE_PORT=16901
luigid --background --pidfile PIDFILE --logdir logs/ --state-path STATE --port $PIPELINE_PORT

# Disable the db.config symlink
ln -s -f /dev/null db.config

# Get rid of symlink if stale/still exists.
touch commits
rm commits
