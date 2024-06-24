package com.orderservice.payment.controller.req;

import com.orderservice.payment.entity.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PaymentMethodRequestDto {
    private PaymentType paymentType;
    private String bank;
    private String accountNumber;
    private String creditCardNumber;

}
