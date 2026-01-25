package com.node5.batchservice.config;

import com.node5.batchservice.reviewsummary.client.CatalogClient;
import com.node5.batchservice.settlement.client.MemberClient;
import com.node5.batchservice.settlement.client.WalletClient;
import com.node5.batchservice.subscription.client.OrderClient;
import com.node5.batchservice.payment.client.PaymentClient;
import com.node5.batchservice.reviewsummary.client.SupportClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {CatalogClient.class, OrderClient.class, PaymentClient.class, SupportClient.class, WalletClient.class, MemberClient.class})
public class OpenFeignConfig {
}
