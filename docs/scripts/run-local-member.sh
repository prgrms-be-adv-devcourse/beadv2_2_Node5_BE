#!/bin/bash

set -a
source ./docs/env_templates/.env.member
set +a

echo "🚀 Starting Member Service with Local Config..."

java -jar member-service/build/libs/member-service-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local
read