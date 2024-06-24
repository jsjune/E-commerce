package com.orderservice.payment.entity;

import com.orderservice.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentMethod extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private String bank;
    private String accountNumber;
    private String creditCardNumber;

    @Builder
    public PaymentMethod(Long id, Long memberId, PaymentType paymentType, String bank,
        String accountNumber, String creditCardNumber) {
        this.id = id;
        this.memberId = memberId;
        this.paymentType = paymentType;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.creditCardNumber = creditCardNumber;
    }
}
