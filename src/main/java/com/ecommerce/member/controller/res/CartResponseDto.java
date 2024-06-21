package com.ecommerce.member.controller.res;

import java.util.List;
import lombok.Getter;

@Getter
public class CartResponseDto {
    List<CartListDto> carts;

    public CartResponseDto(List<CartListDto> carts) {
        this.carts = carts;
    }
}
