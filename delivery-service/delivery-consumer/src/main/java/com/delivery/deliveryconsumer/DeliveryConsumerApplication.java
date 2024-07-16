package com.delivery.deliveryconsumer;

import com.delivery.deliverycore.DeliveryCoreApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Import(DeliveryCoreApplication.class)
public class DeliveryConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryConsumerApplication.class, args);
    }
}
