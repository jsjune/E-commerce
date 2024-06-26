package com.delivery.deliveryconsumer;

import com.deliveryservice.DeliveryServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(DeliveryServiceApplication.class)
public class DeliveryConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryConsumerApplication.class, args);
    }
}
