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
SWITCHES="--spring.profiles.active=chaos-monkey,wavefront,logzio --management.metrics.export.wavefront.api-token=${WAVEFRONT_API_TOKEN} --management.metrics.export.wavefront.uri=${WAVEFRONT_URI:-https://longboard.wavefront.com} --wavefront.application.name=petclinic"

# For Logzio not to randomly fail
rm -rf /tmp/logzio-logback-queue/

echo "Running apps"
mkdir -p target
nohup java -jar spring-petclinic-config-server/target/*.jar --server.port=8888 ${SWITCHES} --wavefront.application.service=config-server > target/config-server.log 2>&1 &
echo "Waiting for config server to start"
sleep 20
nohup java -jar spring-petclinic-discovery-server/target/*.jar --server.port=8761 ${SWITCHES} --wavefront.application.service=discover-server > target/discovery-server.log 2>&1 &
echo "Waiting for discovery server to start"
sleep 20
nohup java -jar spring-petclinic-customers-service/target/*.jar --server.port=8081 ${SWITCHES} --wavefront.application.service=customer-service > target/customers-service.log 2>&1 &
nohup java -jar spring-petclinic-visits-service/target/*.jar --server.port=8082 ${SWITCHES} --wavefront.application.service=visits-service > target/visits-service.log 2>&1 &
nohup java -jar spring-petclinic-vets-service/target/*.jar --server.port=8083 ${SWITCHES} --wavefront.application.service=vets-service > target/vets-service.log 2>&1 &
nohup java -jar spring-petclinic-api-gateway/target/*.jar --server.port=8080 ${SWITCHES} --wavefront.application.service=api-gateway > target/gateway-service.log 2>&1 &
nohup java -jar spring-petclinic-admin-server/target/*.jar --server.port=9090 ${SWITCHES} --wavefront.application.service=admin-server > target/admin-server.log 2>&1 &
echo "Waiting for apps to start"
sleep 60
