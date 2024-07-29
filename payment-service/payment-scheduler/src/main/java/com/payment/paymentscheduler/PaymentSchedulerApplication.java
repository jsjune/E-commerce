package com.payment.paymentscheduler;

import com.payment.paymentcore.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Import(AppConfig.class)
public class PaymentSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentSchedulerApplication.class, args);
    }
}
