package com.node5.memberservice.settlement.presentation;

import com.node5.memberservice.settlement.application.SettlementInternalService;
import com.node5.memberservice.settlement.application.dto.SettlementSourceItem;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Settlement", description = "정산 API")
@RestController
@RequestMapping("/internal/settlements")
@RequiredArgsConstructor
public class SettlementInternalController {

    private final SettlementInternalService settlementInternalService;

    @PostMapping("/source")
    public ResponseEntity<Void> settle(@RequestBody List<SettlementSourceItem> items
    ) {
        settlementInternalService.saveSettlementResource(items);
        return ResponseEntity.ok().build();
    }

    // 진행 중인 정산 조회
    @PostMapping("/in-progress")
    public ResponseEntity<Boolean> hasInProgressSettlement(
            @RequestBody List<UUID> shopIdList
    ) {
        return ResponseEntity.ok(settlementInternalService.hasInProgressSettlement(shopIdList));
    }

}
