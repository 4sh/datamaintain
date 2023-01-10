#!/usr/bin/env bash
set -eEuo pipefail

script_dir=$(dirname "$0")
datamaintain_version=$1

image='docker.pkg.github.com/4sh/datamaintain/datamaintain'

mongo_3_2_dockerfile=$script_dir/Dockerfile/mongo/3/3.2/Dockerfile  # DockerFile with mongo 3.2 to 3.4
mongo_3_6_dockerfile=$script_dir/Dockerfile/mongo/3/3.6/Dockerfile  # DockerFile with mongo 3.6
mongo_4_dockerfile=$script_dir/Dockerfile/mongo/4/Dockerfile  # DockerFile with mongo 4.0 to 4.4
mongo_5_dockerfile=$script_dir/Dockerfile/mongo/5/Dockerfile  # DockerFile with mongo 5.0
mongo_6_dockerfile=$script_dir/Dockerfile/mongo/6/Dockerfile  # DockerFile with mongo 6.0

docker_build_datamaintain() {
  image_build="$image:$datamaintain_version-mongo-$2"
  echo build image "$image_build"
  docker build -t "$image_build" --build-arg MONGO_MAJOR="$2" -f "$1" "$script_dir"/build/distributions/
  echo
  echo check "$image_build"
  docker run --rm -it "$image_build" --help  # Check image startup
  docker run --rm -it --entrypoint bash "$image_build" --login -i -c "complete -p datamaintain"  # Check bash autocomplete
  echo
  echo push "$image_build"
  docker push "$image_build"

  echo "done"
  echo
  echo
}

./"$script_dir"/../../gradlew clean build -Denv=prod
docker_build_datamaintain "$mongo_3_2_dockerfile" 3.2
docker_build_datamaintain "$mongo_3_2_dockerfile" 3.4
docker_build_datamaintain "$mongo_3_6_dockerfile" 3.6
docker_build_datamaintain "$mongo_4_dockerfile" 4.0
docker_build_datamaintain "$mongo_4_dockerfile" 4.2
docker_build_datamaintain "$mongo_4_dockerfile" 4.4
docker_build_datamaintain "$mongo_5_dockerfile" 5.0
docker_build_datamaintain "$mongo_6_dockerfile" 6.0
