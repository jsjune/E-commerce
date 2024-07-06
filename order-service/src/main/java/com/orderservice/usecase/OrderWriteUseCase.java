package com.orderservice.usecase;


import com.orderservice.controller.res.OrderDetailResponseDto;
import com.orderservice.usecase.dto.OrderDtoFromCart;
import com.orderservice.usecase.dto.OrderDtoFromProduct;
import com.orderservice.usecase.dto.RegisterOrderFromCartDto;
import com.orderservice.usecase.dto.RegisterOrderFromProductDto;

public interface OrderWriteUseCase {

    void submitOrderFromProduct(Long memberId, OrderDtoFromProduct command);

    void cancelOrder(Long memberId, Long orderLineId);

    void submitOrderFromCart(Long memberId, OrderDtoFromCart orderDtoFromCart);
}
