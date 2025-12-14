package com.node5.settlementservice.batch;

import com.node5.settlementservice.domain.SettlementSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementScheduler {

    private final JobLauncher jobLauncher;
    private final Job shopSettlementJob;
    private final ThreadPoolTaskExecutor settlementTaskExecutor;
    @Value("${settlement.async.enabled:false}")
    private boolean settlementAsyncEnabled;
    private final SettlementSourceRepository settlementSourceRepository;

    @Scheduled(cron = "${spring.task.scheduling.cron.settlement:0 */3 * * * *}") // 테스트: 3분
    public void runMonthlySettlement() {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);

        // 정산 대상 기간 설정 (YYYY-MM-DD)
        LocalDate startDate = previousMonth.atDay(1);
        LocalDate endDate = previousMonth.atEndOfMonth();

        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTimePlusOneDay = endDate.plusDays(1).atStartOfDay();

        log.info("** Starting monthly settlement jobs for period: {} to {}", startDate, endDate);

        // 1. SettlementSource에서 정산 대상 기간 동안 거래 기록이 있는 판매자 ID만 조회
        List<UUID> shopIds = settlementSourceRepository.findDistinctShopIds(startDateTime, endDateTimePlusOneDay);

//        // 테스트
//        List<UUID> shopIds = List.of(
//                UUID.fromString("10000000-0000-0000-0000-000000000001"),
//                UUID.fromString("20000000-0000-0000-0000-000000000002")
//        );

        if (shopIds.isEmpty()) {
            log.info("** No settlement source found for the period. Skipping job execution.");
            return;
        }

        // 2. 판매자별 Job 호출
        log.info("** Scheduled settlement jobs for {} shops", shopIds.size());
        shopIds.forEach(shopId -> runJobForShop(shopId, startDateStr, endDateStr));
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
                    log.info("** Settlement job triggered for shop {} in period {} - {}", shopId, startDate, endDate);
                } catch (Exception ex) {
                    log.error("** Failed to run settlement job for shop {}", shopId, ex);
                }
            };

            if (settlementAsyncEnabled) {
                settlementTaskExecutor.execute(executeJob);
            } else {
                executeJob.run();
            }
        } catch (Exception ex) {
            log.error("** Failed to run settlement job for shop {}", shopId, ex);
        }
    }
}