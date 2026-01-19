#!/usr/bin/env sh
set -euo pipefail

PROFILE=${MINIKUBE_PROFILE:-node5}
ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/../.." && pwd)
KUBECTL="minikube -p $PROFILE kubectl --"

$KUBECTL apply -k "$ROOT_DIR/k8s"
$KUBECTL get all

for deploy in apigateway catalog-service config-service discovery \
  member-service order-service settlement-service shop-service subscription-service \
  payment-service wallet-service; do
  $KUBECTL rollout status "deploy/${deploy}" || true
done
