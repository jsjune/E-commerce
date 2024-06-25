package com.orderservice.controller.req;

import com.orderservice.usecase.dto.RegisterOrderOfCartDto;
import java.util.List;

public record CartOrderRequestDto(
    List<Long> cartIds
) {
    public RegisterOrderOfCartDto mapToCommand() {
        return new RegisterOrderOfCartDto(cartIds);
    }
}
