#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

echo "Stopping apps"
pkill -9 -f spring-petclinic || echo "Failed to stop any apps"
