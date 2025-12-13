package com.node5.settlementservice.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Pageable;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementScheduler {

    private final JobLauncher jobLauncher;
    private final Job shopSettlementJob;
    private final ThreadPoolTaskExecutor settlementTaskExecutor;
    @Value("${settlement.async.enabled:false}")
    private boolean settlementAsyncEnabled;

    @Scheduled(cron = "${spring.task.scheduling.cron.settlement:0 */3 * * * *}") // 테스트: 3분
    public void runMonthlySettlement() {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);

        // 정산 대상 기간 설정 (YYYY-MM-DD)
        String startDate = previousMonth.atDay(1).format(DateTimeFormatter.ISO_DATE);
        String endDate = previousMonth.atEndOfMonth().format(DateTimeFormatter.ISO_DATE);

        log.info("Starting monthly settlement jobs for period: {} to {}", startDate, endDate);

        // 1. 모든 판매자 ID를 페이지 단위로 조회
//        Pageable pageable = Pageable.ofSize(100);
//        Page<UUID> page = null;
//        do {
//            // page => TODO 판매자 목록 조회 API 호출
//            List<UUID> shopIds = page.getContent();
//            if (shopIds.isEmpty()) break;
//
//            // 2. 판매자별 Job 호출
//            shopIds.forEach(shopId -> runJobForShop(shopId, startDate, endDate));
//            pageable = page.hasNext() ? page.nextPageable() : Pageable.unpaged();
//        } while (page.hasNext());
        //List<UUID> shopIds = createDummyShopIds(5);

        List<UUID> shopIds = List.of(
                UUID.fromString("10000000-0000-0000-0000-000000000001"),
                UUID.fromString("20000000-0000-0000-0000-000000000002")
        );
        shopIds.forEach(shopId -> runJobForShop(shopId, startDate, endDate));
    }

    private List<UUID> createDummyShopIds(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> UUID.fromString(String.format("00000000-0000-0000-0000-0000000000%02d", i + 1)))
                .collect(Collectors.toList());
    }

    private void runJobForShop(UUID shopId, String startDate, String endDate) {
        try {
            Runnable executeJob = () -> {
                try {
                    JobParameters params = new JobParametersBuilder()
                            .addLong("timestamp", System.currentTimeMillis())
                            .addString("shopId", shopId.toString())
                            .addString("startDate", startDate)
                            .addString("endDate", endDate)
                            .toJobParameters();

                    jobLauncher.run(shopSettlementJob, params);
                    log.info("Settlement job triggered for shop {} in period {} - {}", shopId, startDate, endDate);
                } catch (Exception ex) {
                    log.error("Failed to run settlement job for shop {}", shopId, ex);
                }
            };

            if (settlementAsyncEnabled) {
                settlementTaskExecutor.execute(executeJob);
            } else {
                executeJob.run();
            }
        } catch (Exception ex) {
            log.error("Failed to run settlement job for shop {}", shopId, ex);
        }
    }
}