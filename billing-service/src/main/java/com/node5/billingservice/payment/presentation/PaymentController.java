package com.node5.billingservice.payment.presentation;

import com.node5.billingservice.payment.application.PaymentService;
import com.node5.billingservice.payment.application.dto.PaymentFailureInfo;
import com.node5.billingservice.payment.application.dto.PaymentInfo;
import com.node5.billingservice.payment.presentation.dto.PaymentCancelRequest;
import com.node5.billingservice.payment.presentation.dto.PaymentConfirmRequest;
import com.node5.billingservice.payment.presentation.dto.PaymentFailureRequest;
import com.node5.billingservice.payment.presentation.dto.PaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("${api.v1}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 내역 조회", description = "예치금 id에 대한 확정된 결제 정보를 페이지 단위로 조회한다.")
    @GetMapping
    public ResponseEntity<Page<PaymentInfo>> findAll(@RequestHeader("Member-Id") UUID memberId, Pageable pageable) {
        return ResponseEntity.ok(paymentService.findAll(memberId, pageable));
    }

    @Operation(summary = "결제 요청", description = "결제를 요청한다.")
    @PostMapping("/request")
    public ResponseEntity<PaymentInfo> request(@RequestHeader("Member-Id") UUID memberId, @RequestBody PaymentRequest request) {
        return ResponseEntity.status(CREATED).body(paymentService.request(memberId, request.toCommand()));
    }

    @Operation(summary = "결제 확인", description = "결제 완료 후 결제를 승인한다.")
    @PostMapping("/confirm")
    public ResponseEntity<PaymentInfo> confirm(@RequestHeader("Member-Id") UUID memberId, @RequestBody PaymentConfirmRequest request) {
        return ResponseEntity.status(CREATED).body(paymentService.confirm(memberId, request.toCommand()));
    }

    @Operation(summary = "결제 실패 처리", description = "결제 실패 정보를 기록한다.")
    @PostMapping("/failure")
    public ResponseEntity<PaymentFailureInfo> failure(@RequestHeader("Member-Id") UUID memberId, @RequestBody PaymentFailureRequest request) {
        return ResponseEntity.status(CREATED).body(paymentService.failure(memberId, request.toCommand()));
    }

    @Operation(summary = "결제 취소 요청", description = "결제를 취소를 요청한다.")
    @PutMapping("/cancel")
    public ResponseEntity<PaymentInfo> cancel(@RequestHeader("Member-Id") UUID memberId, @RequestBody PaymentCancelRequest request) {
        return ResponseEntity.ok(paymentService.cancel(memberId, request.toCommand()));
    }
}
