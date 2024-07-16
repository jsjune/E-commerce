package com.payment.paymentapi.usecase;


import com.payment.paymentapi.usecase.dto.PaymentMethodResponseDto;
import com.payment.paymentapi.usecase.dto.RegisterPaymentMethodDto;

public interface PaymentMethodUseCase {

    void registerPaymentMethod(Long memberId, RegisterPaymentMethodDto command) throws Exception;

    PaymentMethodResponseDto getPaymentMethods(Long memberId) throws Exception;
}
