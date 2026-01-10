package com.node5.settlementservice.settlement.presentation;

import com.node5.settlementservice.settlement.application.SettlementInternalService;
import com.node5.settlementservice.settlement.application.dto.JobExecutionInfo;
import com.node5.settlementservice.settlement.application.dto.SettlementSourceItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Settlement", description = "정산 API")
@RestController
@RequestMapping("/internal/settlements")
@RequiredArgsConstructor
public class SettlementInternalController {

    private final SettlementInternalService settlementInternalService;

    @Operation(summary = "전체 판매자 월별 정산 배치 실행", description = "지정 연도/월 또는 지난달(default) 기준 모든 판매자(Shop)에 대해 정산 배치 Job을 실행한다.")
    @PostMapping("/run-all")
    public ResponseEntity<String> runAll(
            @RequestParam(value = "yearMonth", required = false) String yearMonth
    ) {
        Long jobExecutionId = settlementInternalService.runAll(yearMonth);
        return ResponseEntity.ok(String.format("Settlement job started for all shops - BatchId: %s", jobExecutionId));
    }

    @Operation(summary = "특정 판매자 월별 정산 배치 실행", description = "지정 연도/월 또는 지난달(default) 기준 특정 판매자(Shop)에 대해 정산 배치 Job을 실행한다.")
    @PostMapping("/run-shop")
    public ResponseEntity<String> runShop(
            @RequestParam("shopId") String shopId,
            @RequestParam(value = "yearMonth", required = false) String yearMonth
    ) {
        Long jobExecutionId = settlementInternalService.runShop(shopId, yearMonth);
        return ResponseEntity.ok(String.format("Settlement job started for shop(%s) - BatchId: %s", shopId, jobExecutionId));
    }

    @Operation(summary = "단일 배치 실행 상태 조회", description = "배치 ID(jobExecutionId)를 통해 해당 Job의 상세 상태를 조회한다.")
    @GetMapping("/status/{jobExecutionId}")
    public ResponseEntity<JobExecutionInfo> getSettlementStatus(
            @PathVariable Long jobExecutionId
    ) {
        return ResponseEntity.ok(settlementInternalService.getStatusByJobExecutionId(jobExecutionId));
    }

    @PostMapping("/source")
    public ResponseEntity<Void> settle(@RequestBody List<SettlementSourceItem> items
    ) {
        settlementInternalService.saveSettlementResource(items);
        return ResponseEntity.ok().build();
    }

}
