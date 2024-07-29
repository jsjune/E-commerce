package com.payment.paymentcore.application.service;


import com.payment.paymentcore.application.service.dto.PaymentMethodResponseDto;
import com.payment.paymentcore.application.service.dto.RegisterPaymentMethodDto;

public interface PaymentMethodUseCase {

    void registerPaymentMethod(Long memberId, RegisterPaymentMethodDto command) throws Exception;

    PaymentMethodResponseDto getPaymentMethods(Long memberId) throws Exception;
}
