package com.order.ordercore.adapter;

import com.order.ordercore.adapter.req.ProcessPaymentRequest;
import com.order.ordercore.application.service.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentClient {
    @PostMapping("/internal/payments/process")
    PaymentDto processPayment(@RequestBody ProcessPaymentRequest paymentRequest);
}
