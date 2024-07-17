package com.order.orderapi.controller.req;

import com.order.orderapi.usecase.dto.RegisterOrderFromCartDto;
import java.util.List;

public record CartOrderRequestDto(
    List<Long> cartIds
) {
    public RegisterOrderFromCartDto mapToCommand() {
        return new RegisterOrderFromCartDto(cartIds);
    }
}
