#!/bin/bash
ps aux | grep [l]uigid | tr -s ' ' | cut -f2 -d" " | xargs kill -9

