#!/bin/bash

set -a
source ./docs/env_templates/.env.support
set +a

echo "🚀 Starting Support Service with Local Config..."

java -jar support-service/build/libs/support-service-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local
read