#!/bin/bash

function build() {
  	docker-compose -f docker-compose.yml -f docker-compose.dev.yml up --build -d
}

function peek() {
  	./run.sh peek
}

function start() {
  	./run.sh start
}

function stop() {
  	./run.sh stop
}

function clean() {
	./run.sh clean
}

"$@"