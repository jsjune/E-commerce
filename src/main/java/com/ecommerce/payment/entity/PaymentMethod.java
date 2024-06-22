package com.ecommerce.payment.entity;

import com.ecommerce.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Member member;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private String bank;
    private String accountNumber;
    private String creditCardNumber;

    @Builder
    public PaymentMethod(Long id, Member member, PaymentType paymentType, String bank,
        String accountNumber, String creditCardNumber) {
        this.id = id;
        this.member = member;
        this.paymentType = paymentType;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.creditCardNumber = creditCardNumber;
    }
}
