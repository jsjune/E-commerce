package com.order.orderscheduler;

import com.order.ordercore.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Import(AppConfig.class)
public class OrderSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderSchedulerApplication.class, args);
    }
}
