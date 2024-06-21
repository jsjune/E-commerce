package com.ecommerce.delivery.entity;

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
    private MemberAddress memberAddress;
    @ManyToOne
    private ProductOrder productOrder;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    private String referenceCode;

}
