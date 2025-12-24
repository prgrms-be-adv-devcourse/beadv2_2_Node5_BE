#!/usr/bin/env sh
set -euo pipefail

PROFILE=${MINIKUBE_PROFILE:-node5}
KUBECTL="minikube -p $PROFILE kubectl --"

$KUBECTL get nodes -o wide
$KUBECTL get pods -o wide
$KUBECTL get svc
