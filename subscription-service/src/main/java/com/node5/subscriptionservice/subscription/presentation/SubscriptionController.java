package com.node5.subscriptionservice.subscription.presentation;

import com.node5.subscriptionservice.subscription.application.SubscriptionService;
import com.node5.subscriptionservice.subscription.application.dto.SubscriptionInfo;
import com.node5.subscriptionservice.subscription.presentation.dto.SubscriptionCreateRequest;
import com.node5.subscriptionservice.subscription.presentation.dto.SubscriptionUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Subscription", description = "구독 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "구독 단건 조회", description = "구독 ID로 구독을 조회합니다.")
    @GetMapping("{id}")
    public ResponseEntity<SubscriptionInfo> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.findById(id));
    }

    @Operation(summary = "구독 전체 조회", description = "회원의 전체 구독 리스트를 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<SubscriptionInfo>> findAllByMemberId(@RequestHeader("Member-Id") UUID memberId, Pageable pageable) {
        return ResponseEntity.ok(subscriptionService.findAllByMemberId(memberId, pageable));
    }

    @Operation(summary = "구독 생성", description = "새로운 구독을 생성합니다")
    @PostMapping
    public ResponseEntity<SubscriptionInfo> create(@RequestBody @Valid SubscriptionCreateRequest request, @RequestHeader("Member-Id") UUID memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.create(request.toCommand(), memberId));
    }

    @Operation(summary = "구독 수정", description = "구독 정보를 수정합니다.")
    @PutMapping("{id}")
    public ResponseEntity<SubscriptionInfo> update(@RequestBody @Valid SubscriptionUpdateRequest request, @PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.update(request.toCommand(), id));
    }

    @Operation(summary = "구독 일시정지", description = "구독을 일시정지합니다.")
    @PatchMapping("{id}/pause")
    public ResponseEntity<SubscriptionInfo> pause(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.pause(id));
    }

    @Operation(summary = "구독 재개", description = "일시정지한 구독을 재개합니다.")
    @PutMapping("{id}/resume")
    public ResponseEntity<SubscriptionInfo> resume(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.resume(id));
    }

    @Operation(summary = "구독 해지", description = "구독을 해지합니다.")
    @DeleteMapping("{id}")
    public ResponseEntity<SubscriptionInfo> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.cancel(id));
    }
}
