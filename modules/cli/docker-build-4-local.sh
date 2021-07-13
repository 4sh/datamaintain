#!/usr/bin/env bash
set -eEuox pipefail

script_dir=$(dirname "$0")
datamaintain_version=$1

image='datamaintain'

mongo_4_dockerfile=$script_dir/Dockerfile/mongo/4/Dockerfile  # DockerFile with mongo 4.0 to 4.4

docker_build_datamaintain() {
  image_build="$image:$datamaintain_version-mongo-$2"
  docker build -t "$image_build" --build-arg MONGO_MAJOR="$2" -f "$1" "$script_dir"/build/distributions/
}

./"$script_dir"/../../gradlew clean build
docker_build_datamaintain "$mongo_4_dockerfile" 4.0