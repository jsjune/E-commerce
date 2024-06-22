package com.ecommerce.payment.usecase;

import com.ecommerce.payment.controller.req.PaymentMethodRequestDto;
import com.ecommerce.payment.controller.res.PaymentMethodResponseDto;

public interface PaymentMethodUseCase {

    void registerPaymentMethod(Long memberId, PaymentMethodRequestDto request) throws Exception;

    PaymentMethodResponseDto getPaymentMethods(Long memberId) throws Exception;
}
