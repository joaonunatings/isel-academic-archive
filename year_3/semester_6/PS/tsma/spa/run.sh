#!/bin/bash

function pull() {
	docker-compose pull
}

function build() {
	docker-compose build
}

function peek() {
	docker-compose logs -f -t
}

function start() {
	docker-compose --env-file .env up -d --no-build
	peek
}

function stop() {
	docker-compose down
}

function clean() {
	stop
	docker image rm joaonunatingscode/tsma:spa
}

"$@"
