package com.paymentservice.controller.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PaymentMethodListDto {
    private Long paymentId;
    private String paymentType;
    private String bank;
    private String accountNumber;
    private String creditCardNumber;
}
