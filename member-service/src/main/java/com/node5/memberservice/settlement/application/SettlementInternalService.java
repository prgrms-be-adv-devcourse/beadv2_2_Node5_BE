package com.node5.memberservice.settlement.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.exception.ExceptionResponseDto;
import com.node5.memberservice.client.CatalogClient;
import com.node5.memberservice.client.OrderClient;
import com.node5.memberservice.settlement.application.dto.SettlementSourceItem;
import com.node5.memberservice.settlement.domain.SettlementProcessStatus;
import com.node5.memberservice.settlement.domain.SettlementSource;
import com.node5.memberservice.settlement.domain.SettlementSourceRepository;
import com.node5.memberservice.settlement.exception.SettlementException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.node5.memberservice.settlement.exception.SettlementErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementInternalService {

    private final SettlementSourceRepository settlementSourceRepository;
    private final OrderClient orderClient;
    private final CatalogClient catalogClient;
    private final ObjectMapper objectMapper;

    // 정산 요청: Order -> SettlementSource 생성
    public void saveSettlementResource(List<SettlementSourceItem> items) {
        List<SettlementSource> sources = items.stream()
                .map(item -> SettlementSource.create(
                        item.productId(),
                        item.shopId(),
                        item.orderId(),
                        item.itemAmount(),
                        item.createdAt(),
                        SettlementProcessStatus.PENDING
                ))
                .toList();
        settlementSourceRepository.saveAll(sources);
    }

    // 진행 중인 정산 조회
    public Boolean hasInProgressSettlement(List<UUID> shopIdList) {
        if (shopIdList == null || shopIdList.isEmpty()) {
            return false;
        }

        // 판매하는 상품 중 정산 대기 중인 것이 존재하는지 OrderItem 테이블 확인
        try {
            List<UUID> productIds = catalogClient.getProductIdsByShopIds(shopIdList).getBody();
            if(productIds != null && !productIds.isEmpty()){
                ResponseEntity<Boolean> hasSettlementPending = orderClient.hasInProgressSettlementPending(productIds);
                if (Boolean.TRUE.equals(hasSettlementPending.getBody())) {
                    return true;
                }
            }
        } catch(FeignException e) {
            throw new SettlementException(SETTLEMENT_FEIGN_ERROR, "message=" + getFeignErrorMessage(e));
        } catch(Exception e) {
            throw new SettlementException(SETTLEMENT_FEIGN_ERROR, "message=" + e.getMessage());
        }

        // SettlementProcessStatus PENDING인 것 존재하는지 SettlementSource 테이블 확인
        return settlementSourceRepository.existsByShopIdInAndStatus(shopIdList, SettlementProcessStatus.PENDING);
    }

    private String getFeignErrorMessage(FeignException e) {
        ExceptionResponseDto err = parseFeignError(e);
        if (err != null) {
            return err.message();
        }
        return " (status:" + e.status() + ", raw:" + safeRawBody(e) + ")";
    }

    private ExceptionResponseDto parseFeignError(FeignException e) {
        return e.responseBody()
                .map(body -> {
                    try {
                        return objectMapper.readValue(e.contentUTF8(), ExceptionResponseDto.class);
                    } catch (Exception ex) {
                        return null;
                    }
                }).orElse(null);
    }

    private String safeRawBody(FeignException e) {
        try {
            return e.contentUTF8().isEmpty() ? "<empty>" : e.contentUTF8();
        } catch (Exception ex) {
            return "<unreadable>";
        }
    }
}
