package com.ecommerce.payment.entity;

import com.ecommerce.member.entity.Member;
import com.ecommerce.order.entity.ProductOrder;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
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
    private ProductOrder productOrder;
    @ManyToOne
    private Member member;
    @ManyToOne
    private PaymentMethod paymentMethod;
    private int totalPrice;
    private int discountPrice;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String referenceCode;

}
