#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

./mvnw clean install -Pwavefront,logzio -T 4
