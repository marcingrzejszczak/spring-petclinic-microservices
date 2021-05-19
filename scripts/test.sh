#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

__DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo -e "\nRunning performance tests\n"
wrk -t2 -c100 -d60 -R 18000 -L http://localhost:8080/api/gateway/owners/1

