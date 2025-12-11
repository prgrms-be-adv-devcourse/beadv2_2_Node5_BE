package com.node5.subscriptionservice.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("com.node5.subscriptionservice.subscription.client")
public class OpenFeignConfig {
}
