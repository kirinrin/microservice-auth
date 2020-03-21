#!/bin/zsh
docker-compose stop
docker-compose pull gateway
docker-compose up -d --build
