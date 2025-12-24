#!/usr/bin/env sh
set -euo pipefail

PROFILE=${MINIKUBE_PROFILE:-node5}
CPUS=${MINIKUBE_CPUS:-4}
MEMORY=${MINIKUBE_MEMORY:-8192}
DRIVER=${MINIKUBE_DRIVER:-}

ARGS="--cpus=$CPUS --memory=$MEMORY"
if [ -n "$DRIVER" ]; then
  ARGS="$ARGS --driver=$DRIVER"
fi

if minikube -p "$PROFILE" status >/dev/null 2>&1; then
  echo "minikube profile '$PROFILE' detected."
fi

minikube start -p "$PROFILE" $ARGS
