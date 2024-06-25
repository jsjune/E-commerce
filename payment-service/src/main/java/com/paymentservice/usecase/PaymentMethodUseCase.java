package com.paymentservice.usecase;

import com.paymentservice.controller.req.PaymentMethodRequestDto;
import com.paymentservice.controller.res.PaymentMethodResponseDto;

public interface PaymentMethodUseCase {

    void registerPaymentMethod(Long memberId, PaymentMethodRequestDto request) throws Exception;

    PaymentMethodResponseDto getPaymentMethods(Long memberId) throws Exception;
}
