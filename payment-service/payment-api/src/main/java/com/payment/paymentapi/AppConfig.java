package com.payment.paymentapi;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"com.payment.paymentapi"})
@Configuration
@EnableAutoConfiguration
public class AppConfig {

}
