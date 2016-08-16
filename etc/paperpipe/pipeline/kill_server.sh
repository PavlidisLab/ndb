#!/bin/bash
ps ux | grep [l]uigid | cut -f2 -d" " | xargs kill -9

