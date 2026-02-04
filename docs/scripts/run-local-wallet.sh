#!/bin/bash

set -a
source ./docs/env_templates/.env.wallet
set +a

echo "🚀 Starting Wallet Service with Local Config..."

java -jar wallet-service/build/libs/wallet-service-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local
read