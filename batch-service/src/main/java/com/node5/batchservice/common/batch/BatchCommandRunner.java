package com.node5.batchservice.common.batch;

import com.node5.batchservice.payment.application.PaymentOutboxCleanupService;
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
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchCommandRunner implements ApplicationRunner {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final JobLauncher jobLauncher;
    private final Job subscriptionOrderJob;
    private final Job monthlyReviewSummaryJob;
    private final PaymentOutboxCleanupService paymentOutboxCleanupService;
    private final ConfigurableApplicationContext context;

    @Value("${batch.run.mode:}")
    private String mode;

    @Value("${batch.run.run-date:}")
    private String runDate;

    @Value("${batch.run.batch-start-date:}")
    private String batchStartDate;

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

    private LocalDate resolveDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return LocalDate.now(KST);
        }
        return LocalDate.parse(raw);
    }
}
