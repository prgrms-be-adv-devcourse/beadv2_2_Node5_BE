package com.node5.orderservice.config;

import com.node5.orderservice.order.client.WalletClient;
import com.node5.orderservice.order.client.CatalogClient;
import com.node5.orderservice.order.client.SettlementClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {WalletClient.class, CatalogClient.class, SettlementClient.class})
public class OpenFeignConfig {

}