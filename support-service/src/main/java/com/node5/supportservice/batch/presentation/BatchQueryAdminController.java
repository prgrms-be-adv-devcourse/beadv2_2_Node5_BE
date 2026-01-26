package com.node5.supportservice.batch.presentation;

import com.node5.supportservice.batch.application.BatchQueryService;
import com.node5.supportservice.batch.application.dto.BatchExecutionResponse;
import com.node5.supportservice.batch.domain.BatchExecutionRow;
import com.node5.supportservice.batch.application.dto.BatchExecutionWithStepsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.v1}/admin/batch")
@RequiredArgsConstructor
public class BatchQueryAdminController {

    private final BatchQueryService batchQueryService;

    @GetMapping("/job-names")
    public ResponseEntity<List<String>> getJobNames() {
        return ResponseEntity.ok(batchQueryService.getJobNames());
    }

    @GetMapping("/executions")
    public ResponseEntity<Page<BatchExecutionResponse>> getExecutions(
            @RequestParam String jobName,
            @PageableDefault(size = 10, sort = "startTime") Pageable pageable
    ) {
        return ResponseEntity.ok(batchQueryService.getExecutions(jobName, pageable));
    }

    @GetMapping("/executions/{executionId}")
    public ResponseEntity<BatchExecutionWithStepsResponse> getExecutionInfo(@PathVariable Long executionId) {
        return ResponseEntity.ok(batchQueryService.getExecutionInfo(executionId));
    }

}
