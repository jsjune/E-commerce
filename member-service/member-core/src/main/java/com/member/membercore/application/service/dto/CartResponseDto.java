package com.member.membercore.application.service.dto;

import com.ecommerce.common.cache.CachingCartListDto;
import java.util.List;

public record CartResponseDto(
    List<CachingCartListDto> carts
) {
}
