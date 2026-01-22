package com.node5.supportservice.batch.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BatchExecutionRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<BatchExecutionRow> findExecutions(String jobName, int limit, int offset) {
        return jdbcTemplate.query("""
            SELECT
                je.JOB_EXECUTION_ID,
                ji.JOB_NAME,
                je.STATUS,
                je.START_TIME,
                je.END_TIME,
                je.EXIT_CODE
            FROM BATCH_JOB_EXECUTION je
            JOIN BATCH_JOB_INSTANCE ji
              ON je.JOB_INSTANCE_ID = ji.JOB_INSTANCE_ID
            WHERE ji.JOB_NAME = ?
            ORDER BY je.CREATE_TIME DESC
            LIMIT ? OFFSET ?
        """, (rs, rowNum) -> new BatchExecutionRow(
                rs.getLong("JOB_EXECUTION_ID"),
                rs.getString("JOB_NAME"),
                rs.getString("STATUS"),
                rs.getTimestamp("START_TIME") != null
                        ? rs.getTimestamp("START_TIME").toLocalDateTime()
                        : null,
                rs.getTimestamp("END_TIME") != null
                        ? rs.getTimestamp("END_TIME").toLocalDateTime()
                        : null,
                rs.getString("EXIT_CODE")
        ), jobName, limit, offset);
    }

    public Optional<BatchExecutionRow> findExecution(Long executionId) {
        return jdbcTemplate.query("""
        SELECT
            je.JOB_EXECUTION_ID,
            ji.JOB_NAME,
            je.STATUS,
            je.START_TIME,
            je.END_TIME,
            je.EXIT_CODE
        FROM BATCH_JOB_EXECUTION je
        JOIN BATCH_JOB_INSTANCE ji
          ON je.JOB_INSTANCE_ID = ji.JOB_INSTANCE_ID
        WHERE je.JOB_EXECUTION_ID = ?
    """, rs -> rs.next()
                ? Optional.of(new BatchExecutionRow(
                rs.getLong("JOB_EXECUTION_ID"),
                rs.getString("JOB_NAME"),
                rs.getString("STATUS"),
                rs.getTimestamp("START_TIME") != null
                        ? rs.getTimestamp("START_TIME").toLocalDateTime()
                        : null,
                rs.getTimestamp("END_TIME") != null
                        ? rs.getTimestamp("END_TIME").toLocalDateTime()
                        : null,
                rs.getString("EXIT_CODE")
        ))
                : Optional.empty(), executionId);
    }

    public List<BatchStepExecutionRow> findStepExecutions(Long executionId) {
        return jdbcTemplate.query("""
        SELECT
            STEP_NAME,
            STATUS,
            READ_COUNT,
            WRITE_COUNT,
            FILTER_COUNT,
            COMMIT_COUNT,
            ROLLBACK_COUNT,
            EXIT_MESSAGE
        FROM BATCH_STEP_EXECUTION
        WHERE JOB_EXECUTION_ID = ?
        ORDER BY STEP_EXECUTION_ID
    """, (rs, rowNum) -> new BatchStepExecutionRow(
                rs.getString("STEP_NAME"),
                rs.getString("STATUS"),
                rs.getInt("READ_COUNT"),
                rs.getInt("WRITE_COUNT"),
                rs.getInt("FILTER_COUNT"),
                rs.getInt("COMMIT_COUNT"),
                rs.getInt("ROLLBACK_COUNT"),
                rs.getString("EXIT_MESSAGE")
        ), executionId);
    }

    public long countExecutions(String jobName) {
        return Objects.requireNonNull(
                jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM BATCH_JOB_EXECUTION je
                JOIN BATCH_JOB_INSTANCE ji
                  ON je.JOB_INSTANCE_ID = ji.JOB_INSTANCE_ID
                WHERE ji.JOB_NAME = ?
            """, Long.class, jobName)
        );
    }
}
