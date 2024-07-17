package com.order.orderapi.usecase;


import com.order.orderapi.usecase.dto.OrderDtoFromCart;
import com.order.orderapi.usecase.dto.OrderDtoFromProduct;

public interface OrderWriteUseCase {

    void submitOrderFromProduct(Long memberId, OrderDtoFromProduct command);

    void cancelOrder(Long memberId, Long orderLineId);

    void submitOrderFromCart(Long memberId, OrderDtoFromCart orderDtoFromCart);
}
