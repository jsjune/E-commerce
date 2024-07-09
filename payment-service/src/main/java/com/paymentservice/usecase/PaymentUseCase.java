package com.paymentservice.usecase;


import com.paymentservice.usecase.dto.ProcessPaymentDto;

public interface PaymentUseCase {

    Long processPayment(ProcessPaymentDto command)
        throws Exception;
}
