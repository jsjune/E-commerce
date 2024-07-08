package com.memberservice.usecase.dto;

import com.ecommerce.common.cache.CartListDto;
import java.util.List;

public record CartResponseDto(
    List<CartListDto> carts
) {
}
