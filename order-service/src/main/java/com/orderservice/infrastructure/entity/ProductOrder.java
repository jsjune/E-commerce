package com.orderservice.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOrder extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    @Enumerated(EnumType.STRING)
    private ProductOrderStatus productOrderStatus;
    @OneToMany(mappedBy = "productOrder")
    private List<OrderLine> orderLines;
    private Long totalPrice;
    private Long totalDiscount;

    @Builder
    public ProductOrder(Long id, Long memberId, ProductOrderStatus productOrderStatus, List<OrderLine> orderLines,
        Long totalPrice, Long totalDiscount) {
        this.id = id;
        this.memberId = memberId;
        this.productOrderStatus = productOrderStatus;
        this.orderLines = orderLines == null ? new ArrayList<>() : orderLines;
        this.totalPrice = totalPrice;
        this.totalDiscount = totalDiscount;
    }

    public void addOrderLine(OrderLine orderLine) {
        this.orderLines.add(orderLine);
        orderLine.assignToOrder(this);
    }

    public void finalizeOrder(ProductOrderStatus productOrderStatus, Long finalTotalPrice,
        Long finalTotalDiscount) {
        this.productOrderStatus = productOrderStatus;
        this.totalPrice = finalTotalPrice;
        this.totalDiscount = finalTotalDiscount;
    }

    public void cancelOrder(Long price, Long discount) {
        this.totalPrice -= price;
        this.totalDiscount -= discount;
    }

    public void assignTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void rollback(Long totalPrice, Long totalDiscount) {
        this.totalPrice -= totalPrice;
        this.totalDiscount -= totalDiscount;
    }
}
