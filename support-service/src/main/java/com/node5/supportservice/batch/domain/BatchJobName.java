package com.node5.supportservice.batch.domain;

import com.node5.supportservice.batch.exception.BatchQueryErrorCode;
import com.node5.supportservice.batch.exception.BatchQueryException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BatchJobName {
    MONTHLY_REVIEW_SUMMARY("monthlyReviewSummaryJob"),
    SUBSCRIPTION_ORDER("subscriptionOrderJob");

    BatchJobName(String jobName) {
        this.jobName = jobName;
    }

    private final String jobName;

    public static void fromJobName(String jobName) {
        Arrays.stream(values())
                .filter(v -> v.jobName.equals(jobName))
                .findFirst()
                .orElseThrow(() ->
                        new BatchQueryException(BatchQueryErrorCode.UNKNOWN_BATCH_JOB_NAME)
                );
    }

}
