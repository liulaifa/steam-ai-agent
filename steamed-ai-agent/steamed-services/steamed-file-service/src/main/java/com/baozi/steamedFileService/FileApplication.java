package com.baozi.steamedFileService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

//排除数据源
@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class},
        scanBasePackages = {"com.baozi"})
public class FileApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }
}
