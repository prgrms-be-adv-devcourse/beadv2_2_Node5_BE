package com.node5.settlementservice.settlement.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementPayoutStatus {
    PENDING("지급 대기 중"),
    PAID("지급 완료"),
    FAILED("지급 실패");

    private final String description;
}
