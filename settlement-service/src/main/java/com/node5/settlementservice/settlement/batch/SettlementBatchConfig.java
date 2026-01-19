package com.node5.settlementservice.settlement.batch;

import com.node5.settlementservice.settlement.batch.dto.SettlementAggregateDto;
import com.node5.settlementservice.settlement.client.WalletClient;
import com.node5.settlementservice.settlement.client.ShopClient;
import com.node5.settlementservice.settlement.client.dto.WalletSettleInfo;
import com.node5.settlementservice.settlement.client.dto.WalletSettleRequest;
import com.node5.settlementservice.settlement.domain.SettlementPayoutStatus;
import com.node5.settlementservice.settlement.domain.SettlementResult;
import com.node5.settlementservice.settlement.domain.SettlementResultRepository;
import com.node5.settlementservice.settlement.domain.SettlementSourceRepository;
import feign.FeignException;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class SettlementBatchConfig {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.05"); // 수수료율 5% 고정
    private static final int CHUNK_SIZE = 100;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SettlementSourceRepository sourceRepository;
    private final SettlementResultRepository resultRepository;

    @Bean
    public Job shopSettlementJobAft(
            JobRepository jobRepository,
            Step settlementCreateStep,
            Step settlementPayoutStep
    ) {
        return new JobBuilder("shopSettlementJob", jobRepository)
                .start(settlementCreateStep)
                .next(settlementPayoutStep)
                .build();
    }

    /**
     * 정산 배치 실행
     * STEP 1. 정산 데이터 생성
     */
    @Bean
    public Step settlementCreateStep(
            ItemReader<SettlementAggregateDto> settlementCreateReader,
            ItemProcessor<SettlementAggregateDto, SettlementResult> settlementCreateProcessor,
            ItemWriter<SettlementResult> settlementCreateWriter
    ) {
        return new StepBuilder("settlementCreateStep", jobRepository)
                .<SettlementAggregateDto, SettlementResult>chunk(CHUNK_SIZE, transactionManager)
                .reader(settlementCreateReader)
                .processor(settlementCreateProcessor)
                .writer(settlementCreateWriter)
                .build();
    }

    // Step 1 - Reader: 판매자별 정산 대상 매출액 집계
    @Bean
    @StepScope
    public JpaPagingItemReader<SettlementAggregateDto> settlementCreateReader(
            @Value("#{jobParameters['startDate']}") String startDate,
            @Value("#{jobParameters['endDate']}") String endDate,
            @Value("#{jobParameters['shopId']}") String shopIdStr,
            EntityManagerFactory entityManagerFactory
    ) {
        // Job param
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).plusDays(1).atStartOfDay();
        UUID targetShopId = (shopIdStr != null && !shopIdStr.isBlank()) ? UUID.fromString(shopIdStr) : null;

        // Query param
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        params.put("shopId", targetShopId);

        return new JpaPagingItemReaderBuilder<SettlementAggregateDto>()
                .name("settlementCreateReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT new com.node5.settlementservice.settlement.batch.dto.SettlementAggregateDto(s.shopId, SUM(s.itemAmount)) " +
                        "FROM SettlementSource s " +
                        "WHERE s.status = 'PENDING' " +
                        "AND s.paidAt >= :start AND s.paidAt < :end " +
                        "AND (:shopId IS NULL OR s.shopId = :shopId) " +
                        "GROUP BY s.shopId " +
                        "ORDER BY s.shopId ASC")
                .parameterValues(params)
                .build();
    }

    // Step 1 - Processor: 수수료 및 정산금 계산, SettlementResult 생성
    @Bean
    @StepScope
    public ItemProcessor<SettlementAggregateDto, SettlementResult> settlementCreateProcessor(
            @Value("#{jobParameters['startDate']}") String startDate,
            @Value("#{jobParameters['endDate']}") String endDate,
            @Value("#{stepExecution.jobExecutionId}") Long jobExecutionId
    ) {
        return dto -> {
            BigDecimal sales = dto.totalAmount();
            BigDecimal fee = sales.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
            BigDecimal payout = sales.subtract(fee).setScale(0, RoundingMode.HALF_UP);

            // SettlementResult 생성
            return SettlementResult.create(
                    dto.shopId(),
                    LocalDate.parse(startDate), LocalDate.parse(endDate),
                    jobExecutionId,
                    LocalDateTime.now(),
                    sales, FEE_RATE, fee, payout,
                    SettlementPayoutStatus.PENDING
            );
        };
    }

    // Step 1 - Writer: SettlementResult 저장, SettlementSource 상태 변경
    @Bean
    @StepScope
    public ItemWriter<SettlementResult> settlementCreateWriter(
            @Value("#{jobParameters['startDate']}") String startDate,
            @Value("#{jobParameters['endDate']}") String endDate
    ) {
        return results -> {
            // 1. SettlementResult 저장
            List<SettlementResult> items = new ArrayList<>(results.getItems());
            resultRepository.saveAll(items);

            // 2. SettlementSource 상태 변경 (PENDING -> COMPLETED)
            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime end = LocalDate.parse(endDate).plusDays(1).atStartOfDay();
            List<UUID> shopIds = items.stream()
                    .map(SettlementResult::getShopId)
                    .toList();
            sourceRepository.bulkUpdateStatus(shopIds, start, end);
        };
    }

    /**
     * 정산 배치 실행
     * TODO) STEP 2. 예치금 지급
     */
    @Bean
    public Step settlementPayoutStep(
            ItemReader<SettlementResult> settlementPayoutReader,
            ItemProcessor<SettlementResult, SettlementResult> settlementPayoutProcessor,
            ItemWriter<SettlementResult> settlementPayoutWriter
    ) {
        return new StepBuilder("settlementPayoutStep", jobRepository)
                .<SettlementResult, SettlementResult>chunk(CHUNK_SIZE, transactionManager)
                .reader(settlementPayoutReader)
                .processor(settlementPayoutProcessor)
                .writer(settlementPayoutWriter)
                .build();
    }

    // Step 2 - Reader: 결제 대기 중인 SettlementResult 데이터 조회
    @Bean
    @StepScope
    public JpaPagingItemReader<SettlementResult> settlementPayoutReader(
            @Value("#{jobParameters['startDate']}") String startDate,
            @Value("#{jobParameters['endDate']}") String endDate,
            @Value("#{jobParameters['shopId']}") String shopIdStr,
            EntityManagerFactory entityManagerFactory
    ) {
        // Query param
        Map<String, Object> params = new HashMap<>();
        params.put("start", LocalDate.parse(startDate));
        params.put("end", LocalDate.parse(endDate));

        UUID targetShopId = (shopIdStr != null && !shopIdStr.isBlank()) ? UUID.fromString(shopIdStr) : null;
        params.put("shopId", targetShopId);

        return new JpaPagingItemReaderBuilder<SettlementResult>()
                .name("settlementPayoutReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT r FROM SettlementResult r " +
                        "WHERE r.status = 'PENDING' " +
                        "AND r.targetStartDate = :start AND r.targetEndDate = :end " +
                        "AND (:shopId IS NULL OR r.shopId = :shopId) " +
                        "ORDER BY r.shopId ASC")
                .parameterValues(params)
                .build();
    }

    // Step 2 - Processor: 정산금 지급
    @Bean
    @StepScope
    public ItemProcessor<SettlementResult, SettlementResult> settlementPayoutProcessor(
            WalletClient walletClient,
            ShopClient shopClient
    ) {
        return result -> {
            try {
                // shopId -> memberId 조회 (shop client 연동)
                ResponseEntity<UUID> shopResponse = shopClient.getMemberIdByShopId(result.getShopId());
                UUID memberId = shopResponse.getBody();

                if(memberId == null) {
                    result.markFailed("memberId 값이 null입니다.");
                    return result;
                }

                // 예치금 지급 (billing client 연동)
                ResponseEntity<WalletSettleInfo> walletResponse = walletClient.settle(
                        memberId,
                        new WalletSettleRequest(result.getId(), result.getPayoutAmount().longValue())
                );

                WalletSettleInfo info = walletResponse.getBody();
                if(info == null){
                    result.markFailed("WalletSettleInfo 값이 null입니다.");
                    return result;
                }

                result.markPaid(info.payoutAt());
            } catch(FeignException e) {
                String msg = "FeignException: " + e.getMessage();
                result.markFailed(msg);
            } catch(Exception e) {
                String msg = "Exception: " + e.getMessage();
                result.markFailed(msg);
            }
            return result;
        };
    }

    // Step 2 - Writer: 정산금 지급 결과 저장
    @Bean
    @StepScope
    public ItemWriter<SettlementResult> settlementPayoutWriter() {
        return chunk -> {
            List<SettlementResult> items = new ArrayList<>(chunk.getItems());
            resultRepository.saveAll(items);
        };
    }

}
