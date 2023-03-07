#!/usr/bin/env bash
set -eEuo pipefail
IFS=$'\n\t'

script_directory="$(cd "$(dirname "$(readlink -f ${BASH_SOURCE[0]})")" &> /dev/null && pwd)"
mongo_cli_script=$(readlink -f "$script_directory/../../../../../driver-mongo/src/test/resources/mongo-cli/mongo-cli-with-docker.sh")
"$mongo_cli_script" "5.0" "mongo" "$@"
