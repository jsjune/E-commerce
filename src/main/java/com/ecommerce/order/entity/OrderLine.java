package com.ecommerce.order.entity;

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
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ProductOrder productOrder;
    @ManyToOne
    private Product product;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private OrderLineStatus status;
    private Long paymentId;
    private Long deliveryId;

    @Builder
    public OrderLine(Long id, ProductOrder productOrder, Product product, int quantity,
        OrderLineStatus status,Long paymentId, Long deliveryId) {
        this.id = id;
        this.productOrder = productOrder;
        this.product = product;
        this.quantity = quantity;
        this.status = status;
        this.paymentId = paymentId;
        this.deliveryId = deliveryId;
    }

    public void finalizeOrderLine(OrderLineStatus orderLineStatus, Long paymentId, Long deliveryId) {
        this.status = orderLineStatus;
        this.paymentId = paymentId;
        this.deliveryId = deliveryId;
    }

    public void assignToOrder(ProductOrder productOrder) {
        this.productOrder = productOrder;
    }
}
