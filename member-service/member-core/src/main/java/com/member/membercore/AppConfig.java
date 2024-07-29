package com.member.membercore;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
@EnableCaching
@EnableAsync
public class AppConfig {

}
