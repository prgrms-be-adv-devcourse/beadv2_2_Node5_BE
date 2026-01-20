package com.node5.batchservice.subscription.batch;

import com.node5.batchservice.subscription.batch.dto.SubscriptionBatchResult;
import com.node5.batchservice.subscription.client.OrderClient;
import com.node5.batchservice.subscription.client.OrderSubscriptionBatchClient;
import com.node5.batchservice.subscription.client.dto.SubscriptionBatchTarget;
import com.node5.batchservice.subscription.infrastructure.kafka.producer.SubscriptionOrderBatchResultProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class SubscriptionOrderBatchJobConfig {

    private static final int CHUNK_SIZE = 100;

    private final OrderSubscriptionBatchClient orderSubscriptionBatchClient;
    private final OrderClient orderClient;
    private final SubscriptionOrderBatchResultProducer resultProducer;

    @Bean
    public Job subscriptionOrderJob(JobRepository jobRepository, Step subscriptionOrderStep) {
        return new JobBuilder("subscriptionOrderJob", jobRepository)
                .start(subscriptionOrderStep)
                .build();
    }

    @Bean
    public Step subscriptionOrderStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      ItemReader<SubscriptionBatchTarget> subscriptionBatchTargetReader,
                                      ItemProcessor<SubscriptionBatchTarget, SubscriptionBatchResult> subscriptionOrderItemProcessor,
                                      ItemWriter<SubscriptionBatchResult> subscriptionOrderBatchResultWriter) {
        return new StepBuilder("subscriptionOrderStep", jobRepository)
                .<SubscriptionBatchTarget, SubscriptionBatchResult>chunk(CHUNK_SIZE, transactionManager)
                .reader(subscriptionBatchTargetReader)
                .processor(subscriptionOrderItemProcessor)
                .writer(subscriptionOrderBatchResultWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<SubscriptionBatchTarget> subscriptionBatchTargetReader(
            @Value("#{jobParameters['runDate']}") String runDate) {
        return new SubscriptionBatchTargetReader(orderSubscriptionBatchClient, runDate, CHUNK_SIZE);
    }

    @Bean
    @StepScope
    public ItemProcessor<SubscriptionBatchTarget, SubscriptionBatchResult> subscriptionOrderItemProcessor(
            @Value("#{jobParameters['runDate']}") String runDate) {
        return new SubscriptionOrderItemProcessor(orderClient, LocalDate.parse(runDate));
    }

    @Bean
    @StepScope
    public ItemWriter<SubscriptionBatchResult> subscriptionOrderBatchResultWriter(
            @Value("#{jobParameters['runDate']}") String runDate) {
        return new SubscriptionOrderBatchResultWriter(resultProducer, LocalDate.parse(runDate));
    }
}
