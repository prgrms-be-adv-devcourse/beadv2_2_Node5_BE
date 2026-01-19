package com.node5.subscriptionservice.config;

import com.node5.subscriptionservice.subscription.client.ProductClient;
import com.node5.subscriptionservice.subscription.client.MemberClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {ProductClient.class, MemberClient.class})
public class OpenFeignConfig {
}
