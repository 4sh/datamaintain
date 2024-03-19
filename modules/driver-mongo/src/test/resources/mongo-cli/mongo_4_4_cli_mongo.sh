#!/usr/bin/env bash
set -eEuo pipefail
IFS=$'\n\t'

script_directory="$(cd "$(dirname "$(readlink -f ${BASH_SOURCE[0]})")" &> /dev/null && pwd)"
"$script_directory/mongo-cli-with-docker.sh" "4.4" "mongo" "$@"
