#!/bin/bash

set -a
source ./docs/env_templates/.env.batch
set +a

echo "🚀 Starting Batch Service with Local Config..."

java -jar batch-service/build/libs/batch-service-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local
read