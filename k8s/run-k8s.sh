#!/usr/bin/env sh
set -euo pipefail

# Use k3s kubectl if kubectl is not on PATH.
if command -v kubectl >/dev/null 2>&1; then
  KUBECTL="kubectl"
else
  KUBECTL="k3s kubectl"
fi

# Apply all manifests via kustomize.
$KUBECTL apply -k k8s

# Show core resources and wait for deployments to roll out.
$KUBECTL get all

for deploy in apigateway catalog-service config-service discovery \
  member-service order-service settlement-service subscription-service \
  payment-service wallet-service; do
  $KUBECTL rollout status "deploy/${deploy}" || true
done
