package com.product.productscheduler;

import com.product.productcore.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Import(AppConfig.class)
public class ProductSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductSchedulerApplication.class, args);
    }
}
