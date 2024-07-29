package com.product.productcore;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan
@Configuration
@EnableAutoConfiguration
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@EnableAsync
public class AppConfig {

}
