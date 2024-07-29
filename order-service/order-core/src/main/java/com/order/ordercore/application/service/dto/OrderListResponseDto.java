package com.order.ordercore.application.service.dto;


import java.util.List;

public record OrderListResponseDto(List<OrderDetailResponseDto> orders) {

}

