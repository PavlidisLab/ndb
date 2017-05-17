# README #

This document explains how to start the pipeline .

### What is this repository for? ###

* Quick summary
* Version


### How do I get set up? ###

0) Create/Launch virtual environment

  In paperpipe/ :
  
```
#!bash

virtualenv venv
source venv/bin/activate

# Install modules to virtual environment
pip install luigi
pip install simplejson
pip install petl
pip install pymysql
pip install hgvs
pip install openpyxl
```
1) Update the database configuration file in paperpipe/pipeline/db.config using your MySQL credentials:
```
dbuser=administrator
dbpass=aSafePassword456
db=ndbtest
host=localhost
```

2) Launch pipeline server

```
#!bash
$ ./start_server.sh
$ ./poll.sh
mbelmad+ 13526  0.0  0.1  80184 20500 ?        S    10:49   0:00 /home/mbelmadani/development/paperpipe/venv/bin/python /home/mbelmadani/development/paperpipe/venv/bin/luigid --background --pidfile PIDFILE --logdir logs/ --state-path STATE
```
start_server.sh will boot up the pipeline server (process name luigid). You can also use kill_server.sh to stop it. The poll.sh script checks if there's an instance of the server running.
By default the pipeline web interface is accessible from: http://localhost:8082/static/visualiser/index.html

3) Launch a task on the pipeline

The tasks are located in paperpipe/pipeline/flows/tasks.py. Each class is a type of tasks.

```
#!bash
./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx LoadPaper
./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx LoadRawKV
./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx LoadRawVariant
./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx LoadVariant


./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx ClearPaper
./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx ClearRawKV
./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx ClearRawVariant
./schedule_task.sh ../exampledata/dyrk1a/variants.xlsx ClearVariant

```

### Who do I talk to? ###

* Manuel Belmadani <manuel.belmadani@ubc.ca>
* Other community or team contact
