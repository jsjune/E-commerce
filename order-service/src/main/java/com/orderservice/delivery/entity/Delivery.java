package com.orderservice.delivery.entity;

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
public class Delivery extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    @ManyToOne
    private DeliveryAddress deliveryAddress;
    @ManyToOne
    private OrderLine orderLine;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    private String referenceCode;

    @Builder
    public Delivery(Long id, Long productId, String productName, int quantity, DeliveryAddress deliveryAddress, OrderLine orderLine,
        DeliveryStatus status, String referenceCode) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.deliveryAddress = deliveryAddress;
        this.orderLine = orderLine;
        this.status = status;
        this.referenceCode = referenceCode;
    }
}
