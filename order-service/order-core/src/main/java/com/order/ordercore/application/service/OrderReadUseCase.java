package com.order.ordercore.application.service;


import com.order.ordercore.application.service.dto.OrderDetailResponseDto;
import com.order.ordercore.application.service.dto.OrderListResponseDto;
import com.order.ordercore.application.service.dto.RegisterOrderFromCartDto;
import com.order.ordercore.application.service.dto.RegisterOrderFromProductDto;

public interface OrderReadUseCase {
    OrderDetailResponseDto getOrderFromCart(Long memberId, RegisterOrderFromCartDto command);

    OrderDetailResponseDto getOrderFromProduct(Long memberId, RegisterOrderFromProductDto command);

    OrderDetailResponseDto getOrder(Long memberId, Long orderId);

    OrderListResponseDto getOrders(Long memberId);
}
