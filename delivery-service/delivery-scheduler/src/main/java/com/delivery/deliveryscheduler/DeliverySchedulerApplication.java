package com.delivery.deliveryscheduler;

import com.delivery.deliverycore.DeliveryCoreApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Import(DeliveryCoreApplication.class)
public class DeliverySchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliverySchedulerApplication.class, args);
    }
}
