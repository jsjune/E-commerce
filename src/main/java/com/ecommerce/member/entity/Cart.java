package com.ecommerce.member.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Cart {
    private String name;
    private int quantity;
    private int price;
    private String thumbnailUrl;
    private Long productId;
}
