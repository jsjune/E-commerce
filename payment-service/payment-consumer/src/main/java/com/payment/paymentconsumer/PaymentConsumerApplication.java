package com.payment.paymentconsumer;

import com.payment.paymentcore.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AppConfig.class)
public class PaymentConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentConsumerApplication.class, args);
    }
}
