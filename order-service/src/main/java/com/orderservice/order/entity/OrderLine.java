package com.orderservice.order.entity;

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
public class OrderLine extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ProductOrder productOrder;
    private Long productId;
    private String productName;
    private int price;
    private int quantity;
    private String thumbnailUrl;
    private int discount;
    @Enumerated(EnumType.STRING)
    private OrderLineStatus orderLineStatus;
    private Long paymentId;
    private Long deliveryId;

    @Builder
    public OrderLine(Long id, ProductOrder productOrder, Long productId,String productName,int price, int quantity,String thumbnailUrl,
        int discount, OrderLineStatus orderLineStatus,Long paymentId, Long deliveryId) {
        this.id = id;
        this.productOrder = productOrder;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.thumbnailUrl = thumbnailUrl;
        this.discount = discount;
        this.orderLineStatus = orderLineStatus;
        this.paymentId = paymentId;
        this.deliveryId = deliveryId;
    }

    public void finalizeOrderLine(OrderLineStatus orderLineStatus, Long paymentId, Long deliveryId) {
        this.orderLineStatus = orderLineStatus;
        this.paymentId = paymentId;
        this.deliveryId = deliveryId;
    }

    public void assignToOrder(ProductOrder productOrder) {
        this.productOrder = productOrder;
    }

    public void cancelOrderLine() {
        this.orderLineStatus = OrderLineStatus.CANCELLED;
    }
}
