package com.payment.paymentcore.infrastructure.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderLineId;
    private Long memberId;
    @ManyToOne
    private PaymentMethod paymentMethod;
    private Long totalPrice;
    private Long discountPrice;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String referenceCode;

    public void rollbackCancel() {
        this.paymentStatus = PaymentStatus.CANCELED;
    }
}
