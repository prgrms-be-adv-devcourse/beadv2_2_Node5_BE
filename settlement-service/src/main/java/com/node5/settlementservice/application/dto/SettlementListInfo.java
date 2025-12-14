package com.node5.settlementservice.application.dto;

import com.node5.common.domain.PageInfoDto;
import com.node5.settlementservice.domain.SettlementResult;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public record SettlementListInfo(
        PageInfoDto pageInfo,
        List<SettlementListDetailInfo> settlementList
) {

    public record SettlementListDetailInfo(
            UUID settlementId,
            String targetYm,
            String status,
            BigDecimal salesAmount,
            BigDecimal feeRate,
            BigDecimal feeAmount,
            BigDecimal payoutAmount,
            String payoutDate
    ) {
        private static final DateTimeFormatter YM_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM");
        private static final DateTimeFormatter PAYOUT_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

        public static SettlementListDetailInfo from(SettlementResult settlement){
            String targetYm = settlement.getTargetStartDate().format(YM_FORMATTER);

            return new SettlementListDetailInfo(
                    settlement.getId(),
                    targetYm,
                    settlement.getStatus().getDescription(),
                    settlement.getSalesAmount(),
                    settlement.getFeeRate(),
                    settlement.getFeeAmount(),
                    settlement.getPayoutAmount(),
                    settlement.getPayoutAt() != null ? settlement.getPayoutAt().format(PAYOUT_TIME_FORMATTER) : null
            );
        }
    }

    public static SettlementListInfo from(PageInfoDto pageInfo, List<SettlementListDetailInfo> settlementList) {
        return new SettlementListInfo(pageInfo, settlementList);
    }
}
