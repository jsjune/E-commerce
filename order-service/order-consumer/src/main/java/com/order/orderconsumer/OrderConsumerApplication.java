package com.order.orderconsumer;

import com.orderservice.OrderServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Import(OrderServiceApplication.class)
public class OrderConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderConsumerApplication.class, args);
    }
}
