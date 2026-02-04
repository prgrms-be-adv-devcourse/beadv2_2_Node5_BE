#!/bin/bash

set -a
source ./docs/env_templates/.env.order
set +a

echo "🚀 Starting Order Service with Local Config..."

java -jar order-service/build/libs/order-service-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local
read