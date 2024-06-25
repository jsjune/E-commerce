package com.orderservice.adapter;

import com.orderservice.adapter.res.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "productClient", url = "${productClient.url}")
public interface ProductClient {
    @GetMapping("/internal/products/{productId}")
    ProductDto getProduct(@PathVariable Long productId);
    @PostMapping("/internal/products/{productId}/decrease")
    Boolean decreaseStock(@PathVariable Long productId,@RequestParam int quantity);
    @PostMapping("/internal/products/{productId}/increment")
    Boolean incrementStock(@PathVariable Long productId,@RequestParam int quantity);
}
