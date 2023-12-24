@echo off

CALL :%~1
GOTO :eof

:pull
docker-compose pull
GOTO :eof

:build
docker-compose build
GOTO :eof

:peek
docker-compose logs -f -t
GOTO :eof

:start
docker-compose --env-file .env up -d --no-build
CALL :peek
GOTO :eof

:stop
docker-compose down
GOTO :eof

:clean
CALL :stop
docker image rm joaonunatingscode/tsma:spa
GOTO :eof
