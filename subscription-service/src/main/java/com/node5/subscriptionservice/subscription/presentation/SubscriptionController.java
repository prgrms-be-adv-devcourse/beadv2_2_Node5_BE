package com.node5.subscriptionservice.subscription.presentation;

import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PagedResponseDto;
import com.node5.subscriptionservice.subscription.application.SubscriptionService;
import com.node5.subscriptionservice.subscription.application.dto.SubscriptionInfo;
import com.node5.subscriptionservice.subscription.presentation.dto.SubscriptionCreateRequest;
import com.node5.subscriptionservice.subscription.presentation.dto.SubscriptionUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Subscription", description = "구독 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "구독 단건 조회", description = "구독 ID로 구독을 조회합니다.")
    @GetMapping("{id}")
    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> findById(@PathVariable UUID id) {
        return subscriptionService.findById(id);
    }

    // TODO: 회원ID를 내부에서 확인하도록 수정
    @Operation(summary = "구독 전체 조회", description = "회원의 전체 구독 리스트를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponseDto<PagedResponseDto<SubscriptionInfo>>> findAllByMemberId(@RequestParam UUID memberId, Pageable pageable) {
        return subscriptionService.findAllByMemberId(memberId, pageable);
    }

    @Operation(summary = "구독 생성", description = "")
    @PostMapping
    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> create(@RequestBody SubscriptionCreateRequest request) {
        return subscriptionService.create(request.toCommand());
    }

    @Operation(summary = "구독 수정", description = "")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> update(@RequestBody SubscriptionUpdateRequest request, @PathVariable UUID id) {
        return subscriptionService.update(request.toCommand(), id);
    }

    @Operation(summary = "구독 일시정지", description = "")
    @PatchMapping("{id}/pause")
    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> pause(@PathVariable UUID id) {
        return subscriptionService.pause(id);
    }

    @Operation(summary = "구독 재개", description = "")
    @PutMapping("{id}/resume")
    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> resume(@PathVariable UUID id) {
        return subscriptionService.resume(id);
    }

    @Operation(summary = "구독 삭제", description = "")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> delete(@PathVariable UUID id) {
        return subscriptionService.delete(id);
    }
}
