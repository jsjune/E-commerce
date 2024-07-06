package com.orderservice.adapter;

import com.orderservice.adapter.req.ProcessDeliveryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @GetMapping("/internal/delivery/{deliveryId}/status")
    Boolean deliveryStatusCheck(@PathVariable Long deliveryId);

    @PostMapping("/internal/delivery/process")
    Long processDelivery(@RequestBody ProcessDeliveryRequest request) throws Exception;
}
