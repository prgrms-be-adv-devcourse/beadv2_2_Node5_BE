#!/usr/bin/env sh
set -euo pipefail

PROFILE=${MINIKUBE_PROFILE:-node5}
ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/../.." && pwd)
KUBECTL="minikube -p $PROFILE kubectl --"

$KUBECTL delete -k "$ROOT_DIR/k8s" --ignore-not-found
