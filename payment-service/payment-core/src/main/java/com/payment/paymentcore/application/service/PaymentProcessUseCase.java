package com.payment.paymentcore.application.service;


import com.payment.paymentcore.application.service.dto.ProcessPaymentDto;

public interface PaymentProcessUseCase {

    Long processPayment(ProcessPaymentDto command)
        throws Exception;
}
