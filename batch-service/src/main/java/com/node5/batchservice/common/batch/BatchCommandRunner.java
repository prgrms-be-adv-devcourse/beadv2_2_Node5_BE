package com.node5.batchservice.common.batch;

import com.node5.batchservice.payment.application.PaymentOutboxCleanupService;
import com.node5.batchservice.settlement.domain.SettlementSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchCommandRunner implements ApplicationRunner {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final JobLauncher jobLauncher;
    private final Job subscriptionOrderJob;
    private final Job monthlyReviewSummaryJob;
    private final PaymentOutboxCleanupService paymentOutboxCleanupService;
    private final Job shopSettlementJob;
    private final SettlementSourceRepository settlementSourceRepository;
    private final ThreadPoolTaskExecutor settlementTaskExecutor;
    private final ConfigurableApplicationContext context;

    @Value("${batch.run.mode:}")
    private String mode;

    @Value("${batch.run.run-date:}")
    private String runDate;

    @Value("${batch.run.batch-start-date:}")
    private String batchStartDate;

    @Value("${settlement.async.enabled:false}")
    private boolean settlementAsyncEnabled;

    @Override
    public void run(ApplicationArguments args) {
        if (mode == null || mode.isBlank()) {
            return;
        }

        int exitCode = 0;
        try {
            switch (mode) {
                case "subscription-order" -> runSubscriptionOrder();
                case "review-summary" -> runReviewSummary();
                case "payment-outbox-cleanup" -> runPaymentOutboxCleanup();
                case "settlement" -> runSettlement();
                default -> {
                    log.error("Unknown batch.run.mode: {}", mode);
                    exitCode = 1;
                }
            }
        } catch (Exception ex) {
            log.error("Batch command failed: {}", ex.getMessage(), ex);
            exitCode = 1;
        } finally {
            int code = exitCode;
            System.exit(SpringApplication.exit(context, () -> code));
        }
    }

    private void runSubscriptionOrder() throws Exception {
        LocalDate date = resolveDate(runDate);
        JobParameters params = new JobParametersBuilder()
                .addString("runDate", date.toString())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        log.info("Running subscriptionOrderJob for {}", date);
        jobLauncher.run(subscriptionOrderJob, params);
    }

    private void runReviewSummary() throws Exception {
        LocalDate date = resolveDate(batchStartDate);
        JobParameters params = new JobParametersBuilder()
                .addString("batchStartDate", date.toString())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        log.info("Running monthlyReviewSummaryJob for {}", date);
        jobLauncher.run(monthlyReviewSummaryJob, params);
    }

    private void runPaymentOutboxCleanup() {
        log.info("Running payment outbox cleanup");
        paymentOutboxCleanupService.cleanupOldMessages();
    }

    private void runSettlement() {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);

        LocalDate startDate = previousMonth.atDay(1);
        LocalDate endDate = previousMonth.atEndOfMonth();

        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTimePlusOneDay = endDate.plusDays(1).atStartOfDay();

        List<UUID> shopIds = settlementSourceRepository.findDistinctShopIds(startDateTime, endDateTimePlusOneDay);

        if (shopIds.isEmpty()) {
            log.info("** [월간 정산] 해당 기간 내 정산 대상 데이터가 없으므로 Job 실행없이 종료");
            return;
        }

        log.info("** [월간 정산] 시작 (period: {} ~ {}, target: {} shops)", startDate, endDate, shopIds.size());
        shopIds.forEach(shopId -> runSettlementForShop(shopId, startDateStr, endDateStr));
    }

    private void runSettlementForShop(UUID shopId, String startDate, String endDate) {
        try {
            Runnable executeJob = () -> {
                try {
                    JobParameters params = new JobParametersBuilder()
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
            log.error("** [정산 실행 실패] shopId: {}, errorMsg: {}", shopId, ex.getMessage(), ex);
        }
    }

    private LocalDate resolveDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return LocalDate.now(KST);
        }
        return LocalDate.parse(raw);
    }
}
