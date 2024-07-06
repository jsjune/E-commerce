package com.orderservice.controller.req;

import com.orderservice.usecase.dto.RegisterOrderFromCartDto;
import java.util.List;

public record CartOrderRequestDto(
    List<Long> cartIds
) {
    public RegisterOrderFromCartDto mapToCommand() {
        return new RegisterOrderFromCartDto(cartIds);
    }
}
