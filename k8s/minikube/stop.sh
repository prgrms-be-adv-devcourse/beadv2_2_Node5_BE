#!/usr/bin/env sh
set -euo pipefail

PROFILE=${MINIKUBE_PROFILE:-node5}

minikube -p "$PROFILE" stop
