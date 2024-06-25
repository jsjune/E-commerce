package com.paymentservice.controller.req;

import com.paymentservice.entity.PaymentType;
import com.paymentservice.usecase.dto.RegisterPaymentMethodDto;
import lombok.Builder;

@Builder
public record PaymentMethodRequestDto(
    PaymentType paymentType,
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
