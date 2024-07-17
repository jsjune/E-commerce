package com.order.orderapi.usecase.dto;

import java.util.List;

public record RegisterOrderFromCartDto(
    List<Long> cartIds
) {

}
