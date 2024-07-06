package com.orderservice.usecase;

import com.orderservice.controller.res.OrderDetailResponseDto;
import com.orderservice.controller.res.OrderListResponseDto;
import com.orderservice.usecase.dto.RegisterOrderFromCartDto;
import com.orderservice.usecase.dto.RegisterOrderFromProductDto;

public interface OrderReadUseCase {
    OrderDetailResponseDto getOrderFromCart(Long memberId, RegisterOrderFromCartDto command);

    OrderDetailResponseDto getOrderFromProduct(Long memberId, RegisterOrderFromProductDto command);

    OrderDetailResponseDto getOrder(Long memberId, Long orderId);

    OrderListResponseDto getOrders(Long memberId);
}
