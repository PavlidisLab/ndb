#!/bin/bash
set -eu

# Kill luigi schedulers currently running using default-scheduler-port

ps -U$(whoami) aux \
    | grep [l]uigid \
    | grep $(cat luigi.cfg  \
      | grep default-scheduler-port \
      | cut -f2 -d"=") \
    |  tr -s ' ' \
    | cut -f2 -d" " \
    | xargs kill -9

# Disable the db.config symlink
ln -s -f /dev/null db.config

# Get rid of symlink if stale/still exists.
touch commits
rm commits
