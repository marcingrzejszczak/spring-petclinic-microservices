#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

pkill -9 -f spring-petclinic || echo "Failed to stop any apps"

docker-compose kill || echo "No docker containers are running"

# echo "Running infra"
# docker-compose up -d grafana-server prometheus-server tracing-server
# docker-compose up -d grafana-server prometheus-server

export WAVEFRONT_API_TOKEN="${WAVEFRONT_API_TOKEN:-}"
SWITCHES="--spring.profiles.active=wavefront,logzio"

# For Logzio not to randomly fail
rm -rf /tmp/logzio-logback-queue/

echo "Running apps"
mkdir -p target
nohup java -jar spring-petclinic-config-server/target/*.jar --server.port=8888 ${SWITCHES} --wavefront.application.service=config-server --spring.application > target/config-server.log 2>&1 &
echo "Waiting for config server to start"
sleep 20
nohup java -jar spring-petclinic-discovery-server/target/*.jar --server.port=8761 ${SWITCHES} --wavefront.application.service=discover-server > target/discovery-server.log 2>&1 &
echo "Waiting for discovery server to start"
sleep 20
nohup java -jar spring-petclinic-customers-service/target/*.jar --server.port=8081 ${SWITCHES} > target/customers-service.log 2>&1 &
nohup java -jar spring-petclinic-visits-service/target/*.jar --server.port=8082 ${SWITCHES} > target/visits-service.log 2>&1 &
nohup java -jar spring-petclinic-vets-service/target/*.jar --server.port=8083 ${SWITCHES} > target/vets-service.log 2>&1 &
nohup java -jar spring-petclinic-api-gateway/target/*.jar --server.port=8080 ${SWITCHES} > target/gateway-service.log 2>&1 &
nohup java -jar spring-petclinic-admin-server/target/*.jar --server.port=9090 ${SWITCHES} > target/admin-server.log 2>&1 &
echo "Waiting for apps to start"
sleep 60
echo "Apps should be running"
