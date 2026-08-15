package com.baozi.steamedCartService;

import com.baozi.steamedApi.Interceptor.FeignAuthInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.baozi.steamedApi.client"})
@ComponentScan(basePackages = {"com.baozi"} )
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }
}
