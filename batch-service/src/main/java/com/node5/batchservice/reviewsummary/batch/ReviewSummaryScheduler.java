package com.node5.batchservice.reviewsummary.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewSummaryScheduler {

    private final JobLauncher jobLauncher;
    private final Job monthlyReviewSummaryJob;

    @Scheduled(cron = "0 0 3 1 * ?")
    public void runMonthlyReviewSummaryJob() {
        try {
            LocalDate batchStartDate = LocalDate.now();

            JobParameters params = new JobParametersBuilder()
                    .addString("batchStartDate", batchStartDate.toString())
                    .toJobParameters();

            jobLauncher.run(monthlyReviewSummaryJob, params);
        } catch (Exception e) {
            log.error("MonthlyReviewSummaryJob failed to start", e);
        }
    }
}
