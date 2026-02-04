#!/bin/bash

set -a
source ./docs/env_templates/.env.payment
set +a

echo "🚀 Starting Payment Service with Local Config..."

java -jar payment-service/build/libs/payment-service-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local
read