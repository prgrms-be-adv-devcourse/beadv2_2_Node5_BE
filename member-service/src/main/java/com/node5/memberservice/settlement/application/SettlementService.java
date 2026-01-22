package com.node5.memberservice.settlement.application;

import com.node5.common.domain.PageInfoDto;
import com.node5.memberservice.settlement.application.dto.SettlementListInfo;
import com.node5.memberservice.settlement.domain.SettlementResult;
import com.node5.memberservice.settlement.domain.SettlementResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementResultRepository settlementResultRepository;

    public SettlementListInfo getSettlementHistory(UUID shopId, YearMonth startYm, YearMonth endYm, int page) {
        LocalDate periodStart = startYm.atDay(1);
        LocalDate periodEnd = endYm.atEndOfMonth();

        Pageable pageable = PageRequest.of(page, 12);
        Page<SettlementResult> result = settlementResultRepository.findByShopIdAndTargetStartDateBetweenOrderByTargetEndDateDesc(shopId, periodStart, periodEnd, pageable);
        PageInfoDto pageInfo = new PageInfoDto(result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());

        if(!result.hasContent()){
            return SettlementListInfo.from(pageInfo, Collections.emptyList());
        }

        List<SettlementListInfo.SettlementListDetailInfo> detailInfoList = result.getContent().stream()
                .map(SettlementListInfo.SettlementListDetailInfo::from)
                .toList();

        return SettlementListInfo.from(pageInfo, detailInfoList);
    }

}
