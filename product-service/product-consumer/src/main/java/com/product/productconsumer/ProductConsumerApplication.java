package com.product.productconsumer;

import com.product.productcore.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AppConfig.class)
public class ProductConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductConsumerApplication.class, args);
    }
}
