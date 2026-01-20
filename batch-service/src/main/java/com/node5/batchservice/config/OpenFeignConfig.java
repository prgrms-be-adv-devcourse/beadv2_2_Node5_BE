package com.node5.batchservice.config;

import com.node5.batchservice.subscription.client.OrderClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {OrderClient.class})
public class OpenFeignConfig {
}
