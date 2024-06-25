package com.orderservice.adapter;

import com.orderservice.adapter.res.PaymentDto;
import com.orderservice.adapter.req.ProcessPaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "paymentClient", url = "${paymentClient.url}")
public interface PaymentClient {
    @PostMapping("/internal/payments/process")
    PaymentDto processPayment(@RequestBody ProcessPaymentRequest paymentRequest);
}
