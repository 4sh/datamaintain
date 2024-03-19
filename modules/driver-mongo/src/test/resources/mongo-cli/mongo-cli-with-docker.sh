#!/usr/bin/env bash
set -eEuo pipefail
IFS=$'\n\t'

# Search datamaintain folder for share it into the mongo cli container
script_directory="$(cd "$(dirname "$(readlink -f ${BASH_SOURCE[0]})")" &> /dev/null && pwd)"
datamaintain_workspace=$(readlink -f "$script_directory/../../../../../..")
module_workspace=$(readlink -f "$script_directory/../../../..")

mongo_tag="$1"
shift
mongo_cli="$1"
shift

# Execute mongo shell in the image with asked command
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    docker run --rm \
               --volume "$datamaintain_workspace":"$datamaintain_workspace" \
               --entrypoint "$mongo_cli" \
               --workdir "$module_workspace" \
               --add-host host.docker.internal:host-gateway \
               mongo:"$mongo_tag" "$@"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    docker run --rm \
      --volume "$datamaintain_workspace":"$datamaintain_workspace" \
      --entrypoint "$mongo_cli" \
      --workdir "$module_workspace" \
      mongo:"$mongo_tag" "$@"
else
    echo "Unknown OS $OSTYPE"
    exit 1
fi
