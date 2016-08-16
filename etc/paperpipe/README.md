# README #

This document explains how to start the pipeline .

### What is this repository for? ###

* Quick summary
* Version


### How do I get set up? ###

0) Create/Launch virtual environment


```
#!bash

source venv/bin/activate
```

1) Launch pipeline server

```
#!bash
$ ./start_server.sh
$ ./poll.sh
mbelmad+ 13526  0.0  0.1  80184 20500 ?        S    10:49   0:00 /home/mbelmadani/development/paperpipe/venv/bin/python /home/mbelmadani/development/paperpipe/venv/bin/luigid --background --pidfile PIDFILE --logdir logs/ --state-path STATE
```
start_server.sh will boot up the pipeline server (process name luigid). You can also use kill_server.sh to stop it. The poll.sh script checks if there's an instance of the server running.
By default the pipeline web interface is accessible from: http://localhost:8082/static/visualiser/index.html

2) Launch a task on the pipeline

The tasks are located in paperpipe/pipeline/flows/tasks.py. Each class is a type of tasks.

```
#!bash
./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx LoadPaper
./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx LoadRawKV
./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx LoadRawVariant
./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx LoadVariant

```

### Who do I talk to? ###

* Repo owner or admin (Manuel Belmadani <manuel.belmadani@ubc.ca>)
* Other community or team contact