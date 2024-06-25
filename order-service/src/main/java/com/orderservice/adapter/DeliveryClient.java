package com.orderservice.adapter;

import com.orderservice.adapter.req.ProcessDeliveryRequest;
import com.orderservice.order.entity.OrderLine;
import jakarta.ws.rs.GET;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "deliveryClient", url = "${deliveryClient.url}")
public interface DeliveryClient {

    @GetMapping("/internal/delivery")
    Boolean deliveryStatusCheck(Long deliveryId);

    @PostMapping("/internal/delivery")
    Long processDelivery(@RequestBody ProcessDeliveryRequest request);
}
