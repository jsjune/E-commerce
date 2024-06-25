package com.orderservice.controller.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductOrderRequestDto {
    private Long productId;
    private int quantity;
}
