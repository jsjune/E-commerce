package com.paymentservice.controller.internal;

import com.paymentservice.usecase.PaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/payments")
public class PaymentInternalController {

    private final PaymentUseCase paymentUseCase;
//    @PostMapping("process")
//    public PaymentDto processPayment(@RequestBody ProcessPaymentRequest request) throws Exception {
//        return paymentUseCase.processPayment(request.mapToCommand());
//    }
}
