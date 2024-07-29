package com.delivery.deliveryconsumer;

import com.delivery.deliverycore.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AppConfig.class)
public class DeliveryConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryConsumerApplication.class, args);
    }
}
