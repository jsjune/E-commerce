package com.paymentservice.usecase;

import com.paymentservice.controller.req.PaymentMethodRequestDto;
import com.paymentservice.controller.res.PaymentMethodResponseDto;
import com.paymentservice.usecase.dto.RegisterPaymentMethodDto;

public interface PaymentMethodUseCase {

    void registerPaymentMethod(Long memberId, RegisterPaymentMethodDto command) throws Exception;

    PaymentMethodResponseDto getPaymentMethods(Long memberId) throws Exception;
}
