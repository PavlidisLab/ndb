#!/bin/bash
ps aux | grep [l]uigid | grep $whoami | tr -s ' ' | cut -f2 -d" " | xargs kill -9

