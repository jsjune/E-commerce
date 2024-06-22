package com.ecommerce.order.controller.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductOrderRequestDto {
    private Long productId;
    private int quantity;
}
