package com.node5.settlementservice.settlement.presentation;

import com.node5.settlementservice.settlement.application.SettlementService;
import com.node5.settlementservice.settlement.application.dto.SettlementListInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.UUID;

@Tag(name = "Settlement", description = "정산 API")
@RestController
@RequestMapping("${api.v1}/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @Operation(summary = "판매자 정산 내역 조회", description = "판매자가 요청하는 기간 내의 월별 정산 내역을 조회한다.")
    @GetMapping("/history")
    public ResponseEntity<SettlementListInfo> getSettlementHistory(
            @RequestHeader("Member-Id") UUID shopId,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM") YearMonth startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM") YearMonth endDate,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(settlementService.getSettlementHistory(shopId, startDate, endDate, page));
    }

}
