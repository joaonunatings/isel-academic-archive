#!/bin/bash

TARGET_DIR="/var/cn2122tf_worker"
JAR_NAME="CN2122TF_Worker-1.0-SNAPSHOT-jar-with-dependencies.jar"

echo "Setting up environment variables..."
export GOOGLE_APPLICATION_CREDENTIALS=$TARGET_DIR/GOOGLE_APPLICATION_CREDENTIALS.json

echo "Using GOOGLE_APPLICATION_CREDENTIALS from $(echo $GOOGLE_APPLICATION_CREDENTIALS)"

echo "Launching JAR..."
java -jar $TARGET_DIR/$JAR_NAME 

