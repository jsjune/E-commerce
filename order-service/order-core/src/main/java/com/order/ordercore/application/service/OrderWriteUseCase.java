package com.order.ordercore.application.service;


import com.order.ordercore.application.service.dto.OrderDtoFromCart;
import com.order.ordercore.application.service.dto.OrderDtoFromProduct;

public interface OrderWriteUseCase {

    void submitOrderFromProduct(Long memberId, OrderDtoFromProduct command);

    void cancelOrder(Long memberId, Long orderLineId);

    void submitOrderFromCart(Long memberId, OrderDtoFromCart orderDtoFromCart);
}
