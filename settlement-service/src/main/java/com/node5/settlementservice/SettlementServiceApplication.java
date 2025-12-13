package com.node5.settlementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SettlementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SettlementServiceApplication.class, args);
	}

}
