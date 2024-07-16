package com.memberservice.adapter;


import com.memberservice.usecase.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/internal/products/{productId}")
    ProductDto getProduct(@PathVariable Long productId);
}
