package com.orderservice.payment.usecase;

import com.orderservice.payment.controller.req.PaymentMethodRequestDto;
import com.orderservice.payment.controller.res.PaymentMethodResponseDto;

public interface PaymentMethodUseCase {

    void registerPaymentMethod(Long memberId, PaymentMethodRequestDto request) throws Exception;

    PaymentMethodResponseDto getPaymentMethods(Long memberId) throws Exception;
}
