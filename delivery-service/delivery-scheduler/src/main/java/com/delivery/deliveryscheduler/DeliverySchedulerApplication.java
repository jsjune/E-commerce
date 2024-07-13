package com.delivery.deliveryscheduler;

import com.deliveryservice.DeliveryServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Import(DeliveryServiceApplication.class)
public class DeliverySchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliverySchedulerApplication.class, args);
    }
}
