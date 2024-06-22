package com.ecommerce.delivery.entity;

import com.ecommerce.order.entity.OrderLine;
import com.ecommerce.order.entity.ProductOrder;
import com.ecommerce.product.entity.Product;
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
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Product product;
    @ManyToOne
    private DeliveryAddress deliveryAddress;
    @ManyToOne
    private OrderLine orderLine;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    private String referenceCode;

    @Builder
    public Delivery(Long id, Product product, DeliveryAddress deliveryAddress, OrderLine orderLine,
        DeliveryStatus status, String referenceCode) {
        this.id = id;
        this.product = product;
        this.deliveryAddress = deliveryAddress;
        this.orderLine = orderLine;
        this.status = status;
        this.referenceCode = referenceCode;
    }
}
