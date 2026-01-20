package com.node5.settlementservice.settlement.batch;

import com.node5.settlementservice.settlement.domain.SettlementSourceRepository;
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

    @Scheduled(cron = "${SETTLEMENT_SCHEDULING_CRON:0 */10 * * * *}")
    //@Scheduled(cron = "${spring.task.scheduling.cron.settlement:0 */10 * * * *}")
    public void runMonthlySettlement() {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);

        // 정산 대상 기간 설정 (YYYY-MM-DD)
        LocalDate startDate = previousMonth.atDay(1);
        LocalDate endDate = previousMonth.atEndOfMonth();

        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTimePlusOneDay = endDate.plusDays(1).atStartOfDay();

        // 1. SettlementSource에서 정산 대상 기간 동안 거래 기록이 있는 판매자 ID만 조회
        List<UUID> shopIds = settlementSourceRepository.findDistinctShopIds(startDateTime, endDateTimePlusOneDay);

        if (shopIds.isEmpty()) {
            log.info("** [월간 정산 스케줄러] 해당 기간 내 정산 대상 데이터가 없으므로 Job 실행없이 종료");
            return;
        }

        // 2. 판매자별 Job 호출
        log.info("** [월간 정산 스케줄러] 시작 (period: {} ~ {}, target: {} shops)", startDate, endDate, shopIds.size());
        shopIds.forEach(shopId -> runJobForShop(shopId, startDateStr, endDateStr));
    }

    private void runJobForShop(UUID shopId, String startDate, String endDate) {
        try {
            Runnable executeJob = () -> {
                try {
                    JobParameters params = new JobParametersBuilder()
                            //.addLong("timestamp", System.currentTimeMillis())
                            .addString("shopId", shopId.toString())
                            .addString("startDate", startDate)
                            .addString("endDate", endDate)
                            .toJobParameters();

                    jobLauncher.run(shopSettlementJob, params);
                    log.info("** [정산 Job 완료] shopId: {}, period: {} ~ {}", shopId, startDate, endDate);
                } catch (Exception ex) {
                    log.error("** [정산 Job 오류] shopId: {}, period: {} ~ {}, errorMsg: {}", shopId, startDate, endDate, ex.getMessage(), ex);
                }
            };

            if (settlementAsyncEnabled) {
                settlementTaskExecutor.execute(executeJob);
            } else {
                executeJob.run();
            }
        } catch (Exception ex) {
            log.error("** [정산 스케줄링 실패] shopId: {}, errorMsg: {}", shopId, ex.getMessage(), ex);
        }
    }
}