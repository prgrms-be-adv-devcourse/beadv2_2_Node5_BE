package com.node5.supportservice.reviewsummary.presentation;

import com.node5.supportservice.reviewsummary.application.ReviewSummaryBatchService;
import com.node5.supportservice.reviewsummary.application.dto.JobExecutionResponse;
import com.node5.supportservice.reviewsummary.application.dto.ReviewSummaryExecutionInfoResponse;
import com.node5.supportservice.reviewsummary.application.dto.ReviewSummaryExecutionListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.v1}/admin/review-summary/batch")
@RequiredArgsConstructor
public class ReviewSummaryBatchAdminController {

    private final ReviewSummaryBatchService reviewSummaryBatchService;

    @PostMapping("/run")
    public ResponseEntity<JobExecutionResponse> runReviewSummary() {
        JobExecutionResponse response = reviewSummaryBatchService.runReviewSummary();

        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/executions")
    public ResponseEntity<List<ReviewSummaryExecutionListResponse>> getExecutions(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(reviewSummaryBatchService.getExecutions(pageable));
    }

    @GetMapping("/executions/{executionId}")
    public ResponseEntity<ReviewSummaryExecutionInfoResponse> getExecutionInfo(@PathVariable Long executionId) {
        return ResponseEntity.ok(reviewSummaryBatchService.getExecutionInfo(executionId));
    }

    @PostMapping("/executions/{executionId}/restart")
    public ResponseEntity<JobExecutionResponse> restartExecution(@PathVariable Long executionId) {
        JobExecutionResponse newExecutionId = reviewSummaryBatchService.restartExecution(executionId);

        return ResponseEntity.accepted().body(newExecutionId);
    }

}
