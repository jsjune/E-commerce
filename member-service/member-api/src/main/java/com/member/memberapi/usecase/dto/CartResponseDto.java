package com.member.memberapi.usecase.dto;

import com.ecommerce.common.cache.CachingCartListDto;
import java.util.List;

public record CartResponseDto(
    List<CachingCartListDto> carts
) {
}
