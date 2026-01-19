package com.node5.walletservice.wallet.client;

import com.node5.walletservice.wallet.client.dto.TransferRequset;
import com.node5.walletservice.wallet.client.dto.TransferResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
public class MockTransferClient implements TransferClient{

    @Override
    public TransferResponse executeTransfer(TransferRequset request) {
        log.info("[External Bank] 이체 요청 시작 - OrderId: {}, Amount: {}", request.orderId(), request.amount());
        LocalDateTime requestedAt = LocalDateTime.now();

        try {
            // 1. 네트워크 지연 시뮬레이션 (200ms ~ 500ms 사이 랜덤)
            // 로컬 부하 테스트 시 스레드 점유를 관찰하기 위함
            long latency = (long) (Math.random() * 300) + 200;
            Thread.sleep(latency);

            // 2. 에러 확률 주입 (예: 5% 확률로 타임아웃 에러 발생)
            double errorChance = Math.random();
            //23:30 ~ 00:30 까지 은행 점검
            if (java.time.LocalTime.now().isAfter(java.time.LocalTime.of(23,30)) ||
                java.time.LocalTime.now().isBefore(java.time.LocalTime.of(0,30))) {
                log.error("[External Bank] 은행 점검 시간 - 이체 불가");
                return new TransferResponse(TransferStateCode.BANK_MAINTENANCE, null, "은행 점검 시간", requestedAt, null);
            } else if (errorChance < 0.05) {
                log.error("[External Bank] 타임아웃 발생!");
                return new TransferResponse(TransferStateCode.BANK_TIMEOUT, null, "은행 서버 응답 지연", requestedAt, null);
            } else if (errorChance < 0.08) { // 3% 확률로 계좌 오류
                return new TransferResponse(TransferStateCode.INVALID_ACCOUNT, null, "유효하지 않은 계좌번호", requestedAt, null);
            }

            // 3. 성공 응답
            String fakeTransactionId = UUID.randomUUID().toString();
            log.info("[External Bank] 이체 성공 - TxId: {}", fakeTransactionId);
            LocalDateTime approvedAt = LocalDateTime.now();
            return new TransferResponse(TransferStateCode.SUCCESS, fakeTransactionId, "정상 처리되었습니다.", requestedAt, approvedAt);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new TransferResponse(TransferStateCode.SYSTEM_ERROR, null, "내부 시스템 오류", requestedAt, null);
        }
    }
}
