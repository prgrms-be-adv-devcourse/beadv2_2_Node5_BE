package com.node5.orderservice.config;

import com.node5.orderservice.order.client.WalletClient;
import com.node5.orderservice.order.client.CatalogClient;
import com.node5.orderservice.order.client.SettlementClient;
import com.node5.orderservice.subscription.client.MemberClient;
import com.node5.orderservice.subscription.client.ProductClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {WalletClient.class, CatalogClient.class, SettlementClient.class, ProductClient.class, MemberClient.class})
public class OpenFeignConfig {

}