#!/usr/bin/env bash

set -euo pipefail

lint_docker_file() {
  echo "Starting hadolinting: $1"
  hadolint $1
  echo "Completed hadolinting: $1"
}

build_image() {
  echo "Start Building Docker Image..."
  TAG=$([ "${CIRCLE_BRANCH}" == "main" ] && echo "0.1.${CIRCLE_BUILD_NUM}" || echo "${CIRCLE_BRANCH}" | sed 's/dependabot\/gradle//g;s/.//')
  echo "New Docker Image Version : ${TAG}"
  echo "${TAG}" > docker-version.txt
  echo "${DOCKER_PASS}" | docker login --username "${DOCKER_USER}" --password-stdin
  docker build -t "${DOCKER_USER}/${DOCKER_IMAGE_SPRINGBOOT_JPA}:${TAG}" .
}
push_image() {
  TAG=$(cat docker-version.txt)
  echo "Will push a docker image with tag : ${TAG}"
  if [ "${CIRCLE_BRANCH}" == "main" ]; then
    echo "Pushing Docker Image latest & ${TAG} ..."
    docker tag "${DOCKER_USER}/${DOCKER_IMAGE_SPRINGBOOT_JPA}:${TAG}" "${DOCKER_USER}/${DOCKER_IMAGE_SPRINGBOOT_JPA}:latest"
    docker push "${DOCKER_USER}/${DOCKER_IMAGE_SPRINGBOOT_JPA}:${TAG}"
    docker push "${DOCKER_USER}/${DOCKER_IMAGE_SPRINGBOOT_JPA}:latest"
  else
    echo "Skipping pushing docker image for non main branch."
  fi
}
"$@"
