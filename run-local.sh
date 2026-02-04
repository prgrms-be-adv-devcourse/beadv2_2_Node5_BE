#!/bin/bash

echo "==============================================="
echo "🚀 Node5 전체 서비스 (새 창 실행)                 "
echo "전부 켜지는 데 시간이 걸릴 수 있습니다.              "
echo "==============================================="

# 'bash' 명령어로 새 창을 띄우고 각 스크립트를 실행합니다.
start bash ./docs/scripts/run-local-config.sh
sleep 10

start bash ./docs/scripts/run-local-discovery.sh
sleep 10

# 비즈니스 서비스들을 각각 새 창으로 띄우기
start bash ./docs/scripts/run-local-apigateway.sh
sleep 10
start bash ./docs/scripts/run-local-batch.sh
sleep 15
start bash ./docs/scripts/run-local-catalog.sh
sleep 15
start bash ./docs/scripts/run-local-member.sh
sleep 15
start bash ./docs/scripts/run-local-order.sh
sleep 15
start bash ./docs/scripts/run-local-payment.sh
sleep 15
start bash ./docs/scripts/run-local-support.sh
sleep 15
start bash ./docs/scripts/run-local-wallet.sh

echo "✅ 모든 서비스가 개별 창에서 실행 중입니다."
read