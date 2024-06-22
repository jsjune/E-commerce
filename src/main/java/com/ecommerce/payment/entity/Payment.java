package com.ecommerce.payment.entity;

import com.ecommerce.member.entity.Member;
import com.ecommerce.order.entity.OrderLine;
import com.ecommerce.order.entity.ProductOrder;
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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private OrderLine orderLine;
    @ManyToOne
    private Member member;
    @ManyToOne
    private PaymentMethod paymentMethod;
    private int totalPrice;
    private int discountPrice;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String referenceCode;

    @Builder
    public Payment(Long id, OrderLine orderLine, Member member, PaymentMethod paymentMethod,
        int totalPrice, int discountPrice, PaymentStatus status, String referenceCode) {
        this.id = id;
        this.orderLine = orderLine;
        this.member = member;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.discountPrice = discountPrice;
        this.status = status;
        this.referenceCode = referenceCode;
    }
}
