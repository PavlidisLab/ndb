PIPELINE_PORT=6901

luigid --background --pidfile PIDFILE --logdir logs/ --state-path STATE --port $PIPELINE_PORT
