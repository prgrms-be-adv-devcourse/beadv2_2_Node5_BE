#!/bin/bash

echo "🚀 Starting Apigateway Service with Local Config..."

java -jar apigateway/build/libs/apigateway-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local
read