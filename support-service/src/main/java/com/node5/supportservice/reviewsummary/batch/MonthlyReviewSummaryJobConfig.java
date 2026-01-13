package com.node5.supportservice.reviewsummary.batch;

import com.node5.supportservice.reviewsummary.batch.dto.ReviewSummaryCommand;
import com.node5.supportservice.reviewsummary.client.LocalLLMChatClient;
import com.node5.supportservice.reviewsummary.client.dto.LocalLLMResponse;
import com.node5.supportservice.reviewsummary.domain.ReviewSummary;
import com.node5.supportservice.reviewsummary.domain.ReviewSummaryRepository;
import com.node5.supportservice.reviewsummary.utils.PromptLoader;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class MonthlyReviewSummaryJobConfig {
    private static final int CHUNK_SIZE = 1;
    private final ReviewSummaryRepository reviewSummaryRepository;
    private final ReviewTestData reviewTestData;
//    private final ReviewService reviewService;

    @Bean
    public Job monthlyReviewSummaryJob(JobRepository jobRepository, Step monthlyReviewSummaryStep) {
        return new JobBuilder("monthlyReviewSummaryJob", jobRepository)
                .validator(parameters -> {
                    if (parameters == null) {
                        throw new JobParametersInvalidException("parameters are required.");
                    }
                    if (parameters.getString("batchStartDate") == null) {
                        throw new JobParametersInvalidException("batchStartDate is required.");
                    }
                })
                .start(monthlyReviewSummaryStep)
                .build();
    }

    @Bean
    public Step monthlyReviewSummaryStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<UUID> productIdsReader,
            ItemProcessor<UUID, ReviewSummaryCommand> monthlyReviewSummaryProcessor,
            ItemWriter<ReviewSummaryCommand> reviewSummaryWriter
    ) {
        return new StepBuilder("monthlyReviewSummaryStep", jobRepository)
                .<UUID, ReviewSummaryCommand>chunk(CHUNK_SIZE, transactionManager)
                .reader(productIdsReader)
                .processor(monthlyReviewSummaryProcessor)
                .writer(reviewSummaryWriter)
                .build();
    }

    // Todo - 예외 처리 잡 실패 or 스킵
    //  - reviewSummaryRepository.findByProductId(productId) DB/트랜잭션 오류
    //  - promptLoader.load("review-summary-v1-local.txt")는 이미 빈에서 실행되니 앱 기동 시 파일 로드 실패 가능
    //  - chatClient.reviewSummary(prompt) 네트워크/타임아웃/서버 오류
    //  - res 또는 res.response()가 null일 때 NPE
    //  - new ReviewSummaryCommand(...)에 startDate가 null이면 이후 처리(Writer/DB)에서 NPE/제약 오류
    @Bean
    @StepScope
    public ItemProcessor<UUID, ReviewSummaryCommand> monthlyReviewSummaryProcessor(
            LocalLLMChatClient chatClient,
            String reviewSummaryTemplate,
            @Value("#{jobParameters['batchStartDate']}") String batchStartDate
    ) {
        return productId -> {
            try {
                LocalDate date;
                try {
                    date = LocalDate.parse(batchStartDate);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid batchStartDate: " + batchStartDate, e);
                }
                LocalDate summaryDate = date.minusMonths(1);
                int summaryYear = summaryDate.getYear();
                int summaryMonth = summaryDate.getMonthValue();

                ReviewSummary lastSummary = reviewSummaryRepository.findByProductId(productId).orElse(null);

                String prevSummary = "없음";
                if (lastSummary != null) {
                    prevSummary = lastSummary.getSummary();
                }

                // Todo - productId, summaryYear, summaryMonth로 정제된 리뷰 읽어옴 아직 없음
                List<String> reviews = reviewTestData.getReviews(productId);
                if (reviews.isEmpty()) {
                    log.info("리뷰 없음, productId={}, {}-{}", productId, summaryYear, summaryMonth);
                    return null;
                }

                String prompt = reviewSummaryTemplate
                        .replace("{{prevSummary}}", prevSummary)
                        .replace("{{reviews}}",
                                reviews.stream()
                                        .map(r -> "- " + r)
                                        .collect(Collectors.joining("\n"))
                        );

                LocalLLMResponse res = chatClient.reviewSummary(prompt);

                LocalDate startDate = LocalDate.of(summaryYear, summaryMonth, 1);
                LocalDate endDate = startDate.plusMonths(1).minusDays(1);

                return new ReviewSummaryCommand(productId, res.response(), startDate, endDate);
            } catch (Exception e) {
                log.warn("LLM 요약 실패, productId: {}", productId, e);
                return null;
            }
        };
    }

    @Bean
    public String reviewSummaryTemplate(PromptLoader promptLoader) {
        return promptLoader.load("review-summary-v1-local.txt");
    }
}
