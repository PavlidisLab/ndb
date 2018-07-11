#!/bin/bash

# List luigi schedulers running using default-scheduler-port

ps aux \
    | grep [l]uigi \
    | grep $(cat luigi.cfg  | grep default-scheduler-port | cut -f2 -d"=")
