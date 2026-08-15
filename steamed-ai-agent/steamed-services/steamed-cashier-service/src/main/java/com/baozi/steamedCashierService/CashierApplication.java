package com.baozi.steamedCashierService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.baozi"})
@EnableFeignClients(basePackages = {"com.baozi.steamedApi.client"})
public class CashierApplication {
    public static void main(String[] args) {
        SpringApplication.run(CashierApplication.class, args);
    }
}
