package com.node5.batchservice.reviewsummary.batch;


import com.node5.batchservice.reviewsummary.application.ReviewSummaryBatchProcessService;
import com.node5.batchservice.reviewsummary.client.SupportClient;
import com.node5.batchservice.reviewsummary.client.dto.ReviewSummaryUpsertRequest;
import com.node5.batchservice.reviewsummary.exception.NoReviewException;
import com.node5.batchservice.reviewsummary.utils.PromptLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class MonthlyReviewSummaryJobConfig {

    private static final String MONTHLY_REVIEW_SUMMARY_JOB_NAME = "monthlyReviewSummaryJob";

    private static final int CHUNK_SIZE = 1;
//    private final ReviewTestData testData;


    @Bean
    public Job monthlyReviewSummaryJob(
            JobRepository jobRepository,
            Step reviewReindexStep,
            Step monthlyReviewSummaryStep
    ) {
        return new JobBuilder(MONTHLY_REVIEW_SUMMARY_JOB_NAME, jobRepository)
                .validator(parameters -> {
                    if (parameters == null) {
                        throw new JobParametersInvalidException("parameters are required.");
                    }
                    String batchStartDate = parameters.getString("batchStartDate");
                    if (batchStartDate == null) {
                        throw new JobParametersInvalidException("batchStartDate is required");
                    }
                    try {
                        LocalDate.parse(batchStartDate);
                    } catch (DateTimeParseException e) {
                        throw new JobParametersInvalidException("Invalid batchStartDate");
                    }
                })
                .start(reviewReindexStep)
                .next(monthlyReviewSummaryStep)
                .build();
    }

    @Bean
    public Step reviewReindexStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SupportClient supportClient
    ) {
        return new StepBuilder("reviewReindexStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    ExecutionContext executionContext = chunkContext.getStepContext()
                            .getStepExecution()
                            .getExecutionContext();

                    Boolean reindexed = executionContext.get("reindexed", Boolean.class);

                    if (Boolean.TRUE.equals(reindexed)) {
                        log.info("리뷰 재인덱싱 이미 완료됨 - skip");
                        return RepeatStatus.FINISHED;
                    }

                    try {
                        supportClient.reindexReviewEmbeddings();
                        executionContext.put("reindexed", true);
                        return RepeatStatus.FINISHED;
                    } catch (Exception e) {
                        log.error("리뷰 재인덱싱 실패", e);
                        throw e;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public Step monthlyReviewSummaryStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<UUID> productIdsReader,
            ItemProcessor<UUID, ReviewSummaryUpsertRequest> monthlyReviewSummaryProcessor,
            ItemWriter<ReviewSummaryUpsertRequest> reviewSummaryWriter
    ) {
        return new StepBuilder("monthlyReviewSummaryStep", jobRepository)
                .<UUID, ReviewSummaryUpsertRequest>chunk(CHUNK_SIZE, transactionManager)
//                .reader(new ListItemReader<>(testData.getProductIds()))
                .reader(productIdsReader)
                .processor(monthlyReviewSummaryProcessor)
                .writer(reviewSummaryWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<UUID, ReviewSummaryUpsertRequest> monthlyReviewSummaryProcessor(
            ReviewSummaryBatchProcessService batchProcessService,
            @Value("#{jobParameters['batchStartDate']}") String batchStartDate
    ) {
        LocalDate date = LocalDate.parse(batchStartDate);
        return productId -> {
            try {
                return batchProcessService.process(productId, date);
            } catch (NoReviewException e) {
                log.info("리뷰 없음 skip, productId={}", productId);
                return null;
            }
        };
    }

    @Bean
    public String reviewSummaryTemplate(PromptLoader promptLoader) {
        return promptLoader.load("review-summary-v1-gpt.txt");
    }
}
