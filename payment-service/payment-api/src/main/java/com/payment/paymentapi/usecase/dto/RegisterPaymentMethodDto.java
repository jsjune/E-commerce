package com.payment.paymentapi.usecase.dto;

import lombok.Builder;

@Builder
public record RegisterPaymentMethodDto(
    String paymentType,
    String bank,
    String accountNumber,
    String creditCardNumber
) {

}
