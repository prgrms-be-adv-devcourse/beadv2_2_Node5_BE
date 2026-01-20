package com.node5.batchservice.config;

import com.node5.batchservice.reviewsummary.client.CatalogClient;
import com.node5.batchservice.reviewsummary.client.SupportClient;
import com.node5.batchservice.subscription.client.OrderClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {OrderClient.class, CatalogClient.class, SupportClient.class})
public class OpenFeignConfig {
}
