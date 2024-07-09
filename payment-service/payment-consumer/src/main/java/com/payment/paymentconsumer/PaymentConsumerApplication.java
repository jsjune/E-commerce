package com.payment.paymentconsumer;

import com.paymentservice.PaymentServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Import(PaymentServiceApplication.class)
public class PaymentConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentConsumerApplication.class, args);
    }
}
