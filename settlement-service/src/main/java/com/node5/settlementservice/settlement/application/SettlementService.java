package com.node5.settlementservice.settlement.application;

import com.node5.common.domain.PageInfoDto;
import com.node5.settlementservice.settlement.application.dto.SettlementListInfo;
import com.node5.settlementservice.settlement.domain.SettlementResult;
import com.node5.settlementservice.settlement.domain.SettlementResultRepository;
import com.node5.settlementservice.settlement.exception.SettlementException;
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

import static com.node5.settlementservice.settlement.exception.SettlementErrorCode.ACCESS_DENIED;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementResultRepository settlementResultRepository;


    public SettlementListInfo getSettlementHistory(String roles, UUID shopId, YearMonth startYm, YearMonth endYm, int page) {
        if(!hasSellerRole(roles)){
            throw new SettlementException(ACCESS_DENIED);
        }

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

    private boolean hasSellerRole(String roles){
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        String[] roleArr = roles.split(",");
        for (String role : roleArr) {
            if ("SELLER".equals(role.trim().toUpperCase())) {
                return true;
            }
        }

        return false;
    }
}
