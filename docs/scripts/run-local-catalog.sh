#!/bin/bash

set -a
source ./docs/env_templates/.env.catalog
set +a

echo "🚀 Starting Catalog Service with Local Config..."

java -jar catalog-service/build/libs/catalog-service-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local
read