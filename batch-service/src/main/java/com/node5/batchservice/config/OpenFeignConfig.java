package com.node5.batchservice.config;

import com.node5.batchservice.subscription.client.OrderClient;
import com.node5.batchservice.subscription.client.OrderSubscriptionBatchClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {OrderClient.class, OrderSubscriptionBatchClient.class})
public class OpenFeignConfig {
}
