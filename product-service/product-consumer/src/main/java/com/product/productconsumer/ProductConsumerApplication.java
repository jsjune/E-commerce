package com.product.productconsumer;

import com.productservice.ProductServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Import(ProductServiceApplication.class)
public class ProductConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductConsumerApplication.class, args);
    }
}
