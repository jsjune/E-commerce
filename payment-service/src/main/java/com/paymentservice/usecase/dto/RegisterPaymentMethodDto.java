package com.paymentservice.usecase.dto;

import com.paymentservice.entity.PaymentType;
import lombok.Builder;

@Builder
public record RegisterPaymentMethodDto(
    PaymentType paymentType,
    String bank,
    String accountNumber,
    String creditCardNumber
) {

}
