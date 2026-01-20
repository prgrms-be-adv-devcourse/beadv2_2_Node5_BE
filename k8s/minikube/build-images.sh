#!/usr/bin/env sh
set -euo pipefail

PROFILE=${MINIKUBE_PROFILE:-node5}
ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/../.." && pwd)

GRADLE_TASKS=${GRADLE_TASKS:-build}
GRADLE_FLAGS=${GRADLE_FLAGS:--x test}

cd "$ROOT_DIR"
./gradlew $GRADLE_TASKS $GRADLE_FLAGS

eval "$(minikube -p "$PROFILE" docker-env)"

build_image() {
  name=$1
  path=$2
  echo "Building node5dev/${name}:latest from ${path}"
  docker build -t "node5dev/${name}:latest" "$path"
}

build_image apigateway "$ROOT_DIR/apigateway"
build_image catalog-service "$ROOT_DIR/catalog-service"
build_image discovery "$ROOT_DIR/discovery"
build_image member-service "$ROOT_DIR/member-service"
build_image order-service "$ROOT_DIR/order-service"
build_image payment-service "$ROOT_DIR/payment-service"
build_image settlement-service "$ROOT_DIR/settlement-service"
build_image wallet-service "$ROOT_DIR/wallet-service"
