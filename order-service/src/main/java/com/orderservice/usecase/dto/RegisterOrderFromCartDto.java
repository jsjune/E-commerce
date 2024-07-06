package com.orderservice.usecase.dto;

import java.util.List;

public record RegisterOrderFromCartDto(
    List<Long> cartIds
) {

}
