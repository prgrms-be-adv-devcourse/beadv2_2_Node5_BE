package com.node5.batchservice.config;

import com.node5.batchservice.subscription.client.OrderClient;
import com.node5.batchservice.subscription.client.SubscriptionBatchClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {OrderClient.class, SubscriptionBatchClient.class})
public class OpenFeignConfig {
}
