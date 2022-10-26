#!/usr/bin/env bash

set -euo pipefail

: "${EKS_NAMESPACE}"
: "${RELEASE_NAME}"
VERSION=latest
echo "Deploying app version : ${VERSION}"
helm upgrade \
--install \
--wait \
--namespace "${EKS_NAMESPACE}" \
--set buildno="${VERSION}" \
--set authUser="${BASIC_AUTH_USERNAME}" \
--set authPassword="${BASIC_AUTH_PASSWORD}" \
--set SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME}" \
--set SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD}" \
--set SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL}" \
--set SPRING_PROFILE_ACTIVE="prod" \
"${RELEASE_NAME}" ./kubernetes/helm-chart
