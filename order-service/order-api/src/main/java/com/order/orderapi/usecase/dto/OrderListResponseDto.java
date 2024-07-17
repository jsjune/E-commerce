package com.order.orderapi.usecase.dto;


import java.util.List;

public record OrderListResponseDto(List<OrderDetailResponseDto> orders) {

}

