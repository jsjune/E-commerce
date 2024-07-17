package com.order.orderapi.usecase;


import com.order.orderapi.usecase.dto.OrderDetailResponseDto;
import com.order.orderapi.usecase.dto.OrderListResponseDto;
import com.order.orderapi.usecase.dto.RegisterOrderFromCartDto;
import com.order.orderapi.usecase.dto.RegisterOrderFromProductDto;

public interface OrderReadUseCase {
    OrderDetailResponseDto getOrderFromCart(Long memberId, RegisterOrderFromCartDto command);

    OrderDetailResponseDto getOrderFromProduct(Long memberId, RegisterOrderFromProductDto command);

    OrderDetailResponseDto getOrder(Long memberId, Long orderId);

    OrderListResponseDto getOrders(Long memberId);
}
