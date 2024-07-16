package com.member.membercore.adapter;


import com.member.membercore.application.service.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/internal/products/{productId}")
    ProductDto getProduct(@PathVariable Long productId);
}
