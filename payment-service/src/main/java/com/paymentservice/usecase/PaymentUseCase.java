package com.paymentservice.usecase;


import com.paymentservice.controller.internal.res.PaymentDto;
import com.paymentservice.usecase.dto.ProcessPaymentDto;

public interface PaymentUseCase {

    PaymentDto processPayment(ProcessPaymentDto command)
        throws Exception;
}
