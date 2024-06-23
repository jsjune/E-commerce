package com.ecommerce.order.entity;

import com.ecommerce.common.BaseTimeEntity;
import com.ecommerce.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    @ManyToOne
    private Member member;
    @Enumerated(EnumType.STRING)
    private ProdcutOrderStatus productOrderStatus;
    @OneToMany(mappedBy = "productOrder")
    private List<OrderLine> orderLines;
    private int totalPrice;
    private int totalDiscount;

    @Builder
    public ProductOrder(Long id, Member member, ProdcutOrderStatus productOrderStatus, List<OrderLine> orderLines,
        int totalPrice, int totalDiscount) {
        this.id = id;
        this.member = member;
        this.productOrderStatus = productOrderStatus;
        this.orderLines = orderLines;
        this.totalPrice = totalPrice;
        this.totalDiscount = totalDiscount;
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
}
