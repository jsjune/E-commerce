package com.order.ordercore.application.service.dto;

import java.util.List;

public record RegisterOrderFromCartDto(
    List<Long> cartIds
) {

}
