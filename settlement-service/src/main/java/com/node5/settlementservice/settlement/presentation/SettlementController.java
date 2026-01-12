package com.node5.settlementservice.settlement.presentation;

import com.node5.settlementservice.settlement.application.SettlementService;
import com.node5.settlementservice.settlement.application.dto.SettlementListInfo;
import com.node5.settlementservice.settlement.exception.SettlementException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.UUID;

import static com.node5.settlementservice.settlement.exception.SettlementErrorCode.INVALID_VALUE;

@Tag(name = "Settlement", description = "정산 API")
@RestController
@RequestMapping("${api.v1}/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @Operation(summary = "판매자 정산 내역 조회", description = "판매자가 요청하는 기간 내의 월별 정산 내역을 조회한다.")
    @GetMapping("/history")
    public ResponseEntity<SettlementListInfo> getSettlementHistory(
            @RequestParam("shopId") UUID shopId,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM") YearMonth startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM") YearMonth endDate,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        if (startDate.isAfter(endDate)) {
            throw new SettlementException(INVALID_VALUE, "조회 시작일(" + startDate + ")은 종료일(" + endDate + ")보다 늦을 수 없습니다.");
        }

        return ResponseEntity.ok(settlementService.getSettlementHistory(shopId, startDate, endDate, page));
    }

}
