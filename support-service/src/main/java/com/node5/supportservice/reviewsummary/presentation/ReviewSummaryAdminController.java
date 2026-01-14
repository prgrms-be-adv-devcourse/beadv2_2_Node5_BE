package com.node5.supportservice.reviewsummary.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("${api.v1}/admin")
@RequiredArgsConstructor
public class ReviewSummaryAdminController {

    private final Job monthlyReviewSummaryJob;
    private final JobLauncher jobLauncher;

    @PostMapping("/batch/review-summary")
    public ResponseEntity<Void> reviewSummaryManualBatch(@RequestParam Long runAt) {
        LocalDate batchStartDate = LocalDate.now();

        JobParameters params = new JobParametersBuilder()
                .addString("batchStartDate", batchStartDate.toString())
                .addLong("runAt", runAt)
                .toJobParameters();

        try {
            jobLauncher.run(monthlyReviewSummaryJob, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().build();
    }
}
