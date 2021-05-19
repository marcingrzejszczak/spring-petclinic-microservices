#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

__DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

. "${__DIR}"/stop_all.sh
. "${__DIR}"/build_all.sh
. "${__DIR}"/run_all.sh
