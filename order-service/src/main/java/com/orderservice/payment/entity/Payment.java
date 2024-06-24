package com.orderservice.payment.entity;

import com.orderservice.order.entity.OrderLine;
import com.orderservice.utils.BaseTimeEntity;
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
public class Payment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private OrderLine orderLine;
    private Long memberId;
    @ManyToOne
    private PaymentMethod paymentMethod;
    private int totalPrice;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String referenceCode;

    @Builder
    public Payment(Long id, OrderLine orderLine, Long memberId, PaymentMethod paymentMethod,
        int totalPrice, PaymentStatus status, String referenceCode) {
        this.id = id;
        this.orderLine = orderLine;
        this.memberId = memberId;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.status = status;
        this.referenceCode = referenceCode;
    }
}
