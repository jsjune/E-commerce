package com.orderservice.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @GetMapping("/internal/delivery/{deliveryId}/status")
    Boolean deliveryStatusCheck(@PathVariable Long deliveryId);

}
