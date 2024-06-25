package com.orderservice.usecase.dto;

import java.util.List;

public record RegisterOrderOfCartDto(
    List<Long> cartIds
) {

}
