package com.node5.paymentservice.payment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.paymentservice.payment.application.dto.*;
import com.node5.paymentservice.payment.domain.*;
import com.node5.paymentservice.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRequestService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    // 결제 요청
    public PaymentRequestInfo request(UUID memberId, PaymentCommand command) {
        // 주문 번호 생성
        String orderId = generateOrderId();

        // Redis에 결제 임시 데이터 저장
        PaymentTemporaryData readyData = new PaymentTemporaryData(memberId, orderId, command.amount());

        // Redis 저장
        saveToRedis(orderId, readyData);

        // 결제 요청 정보 반환
        return PaymentRequestInfo.from(orderId);
    }

    private String generateOrderId() {
        return "ORDER-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    // Redis에 결제 임시 데이터 저장
    private void saveToRedis(String orderId, PaymentTemporaryData data) {
        try {
            String redisKey = "payment:ready:" + orderId;
            String jsonValue = objectMapper.writeValueAsString(data);
            stringRedisTemplate.opsForValue().set(redisKey, jsonValue, Duration.ofMinutes(10));
        } catch (JsonProcessingException e) {
            throw new PaymentException(PAYMENT_REDIS_PROCESS_ERROR);
        }
    }
}
