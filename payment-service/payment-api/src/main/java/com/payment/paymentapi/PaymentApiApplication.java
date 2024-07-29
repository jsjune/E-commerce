package com.payment.paymentapi;

import com.payment.paymentcore.AppConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AppConfig.class})
public class PaymentApiApplication {

}
