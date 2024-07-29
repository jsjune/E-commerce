package com.payment.paymentapi.controller.req;

import com.payment.paymentcore.application.service.dto.RegisterPaymentMethodDto;
import lombok.Builder;

@Builder
public record PaymentMethodRequestDto(
    String paymentType,
    String bank,
    String accountNumber,
    String creditCardNumber
) {
    public RegisterPaymentMethodDto mapToCommand() {
        return RegisterPaymentMethodDto.builder()
            .paymentType(paymentType)
            .bank(bank)
            .accountNumber(accountNumber)
            .creditCardNumber(creditCardNumber)
            .build();
    }
}
