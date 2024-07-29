package com.payment.paymentcore;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@ComponentScan
@Configuration
@EnableAutoConfiguration
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
public class AppConfig {

}
