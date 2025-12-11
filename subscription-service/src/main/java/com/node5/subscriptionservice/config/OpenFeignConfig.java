package com.node5.subscriptionservice.config;

import com.node5.subscriptionservice.subscription.client.OrderClient;
import com.node5.subscriptionservice.subscription.client.ProductClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {OrderClient.class, ProductClient.class})
public class OpenFeignConfig {
}
