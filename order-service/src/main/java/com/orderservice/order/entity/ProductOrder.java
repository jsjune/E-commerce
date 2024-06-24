package com.orderservice.order.entity;

import com.orderservice.utils.BaseTimeEntity;
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
    private ProdcutOrderStatus productOrderStatus;
    @OneToMany(mappedBy = "productOrder")
    private List<OrderLine> orderLines;
    private int totalPrice;
    private int totalDiscount;

    @Builder
    public ProductOrder(Long id, Long memberId, ProdcutOrderStatus productOrderStatus, List<OrderLine> orderLines,
        int totalPrice, int totalDiscount) {
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

    public void finalizeOrder(ProdcutOrderStatus prodcutOrderStatus, int finalTotalPrice,
        int finalTotalDiscount) {
        this.productOrderStatus = prodcutOrderStatus;
        this.totalPrice = finalTotalPrice;
        this.totalDiscount = finalTotalDiscount;
    }

    public void cancelOrder(int price, int discount) {
        this.totalPrice -= price;
        this.totalDiscount -= discount;
    }

    public void assignTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
