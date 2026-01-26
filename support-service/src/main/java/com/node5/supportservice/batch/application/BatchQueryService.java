package com.node5.supportservice.batch.application;


import com.node5.supportservice.batch.application.dto.BatchExecutionResponse;
import com.node5.supportservice.batch.application.dto.BatchExecutionWithStepsResponse;
import com.node5.supportservice.batch.domain.BatchExecutionRepository;
import com.node5.supportservice.batch.domain.BatchExecutionRow;
import com.node5.supportservice.batch.domain.BatchJobName;
import com.node5.supportservice.batch.domain.BatchStepExecutionRow;
import com.node5.supportservice.batch.exception.BatchQueryErrorCode;
import com.node5.supportservice.batch.exception.BatchQueryException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BatchQueryService {

    private final BatchExecutionRepository batchExecutionRepository;

    public Page<BatchExecutionResponse> getExecutions(String jobName, Pageable pageable) {
        BatchJobName.fromJobName(jobName);

        int limit = pageable.getPageSize();
        int offset = pageable.getPageNumber() * limit;

        List<BatchExecutionRow> executions = batchExecutionRepository.findExecutions(jobName, limit, offset);

        List<BatchExecutionResponse> responses = executions.stream().map(BatchExecutionResponse::from).toList();
        long total = batchExecutionRepository.countExecutions(jobName);

        return new PageImpl<>(responses, pageable, total);
    }

    public BatchExecutionWithStepsResponse getExecutionInfo(Long executionId) {
        BatchExecutionRow execution = batchExecutionRepository
                .findExecution(executionId)
                .orElseThrow(() ->
                        new BatchQueryException(
                                BatchQueryErrorCode.JOB_EXECUTION_NOT_FOUND
                        )
                );

        List<BatchStepExecutionRow> steps =
                batchExecutionRepository.findStepExecutions(executionId);

        return BatchExecutionWithStepsResponse.from(execution, steps);
    }

    public List<String> getJobNames() {
        return Arrays.stream(BatchJobName.values()).map(BatchJobName::getJobName).toList();
    }
}
