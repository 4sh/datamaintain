#!/usr/bin/env bash
set -eEuo pipefail
IFS=$'\n\t'

# Use mongo 5.0 image because mongosh start with mongo 5.0
script_directory="$(cd "$(dirname "$(readlink -f ${BASH_SOURCE[0]})")" &> /dev/null && pwd)"
"$script_directory/mongo-cli-with-docker.sh" "5.0" "mongosh" "$@"
