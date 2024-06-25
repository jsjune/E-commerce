package com.paymentservice.usecase;


import com.paymentservice.controller.internal.res.PaymentDto;
import com.paymentservice.usecase.dto.ProcessPayment;

public interface PaymentUseCase {

    PaymentDto processPayment(ProcessPayment command)
        throws Exception;
}
