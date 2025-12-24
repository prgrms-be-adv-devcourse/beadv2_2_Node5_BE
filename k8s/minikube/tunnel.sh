#!/usr/bin/env sh
set -euo pipefail

PROFILE=${MINIKUBE_PROFILE:-node5}

echo "Starting minikube tunnel for profile '$PROFILE'."
minikube -p "$PROFILE" tunnel
