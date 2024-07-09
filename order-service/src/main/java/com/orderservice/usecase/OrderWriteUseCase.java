package com.orderservice.usecase;


import com.orderservice.usecase.dto.OrderDtoFromCart;
import com.orderservice.usecase.dto.OrderDtoFromProduct;

public interface OrderWriteUseCase {

    void submitOrderFromProduct(Long memberId, OrderDtoFromProduct command);

    void cancelOrder(Long memberId, Long orderLineId);

    void submitOrderFromCart(Long memberId, OrderDtoFromCart orderDtoFromCart);
}
