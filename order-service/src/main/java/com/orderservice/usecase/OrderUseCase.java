package com.orderservice.usecase;


import com.orderservice.controller.req.OrderRequest;
import com.orderservice.controller.req.ProductOrderRequestDto;
import com.orderservice.controller.res.OrderDetailResponseDto;
import com.orderservice.controller.res.OrderListResponseDto;
import java.util.List;

public interface OrderUseCase {

    OrderDetailResponseDto registerOrderOfCart(Long memberId, List<Long> cartIds);

    OrderDetailResponseDto registerOrder(Long memberId, ProductOrderRequestDto request);

    void submitOrder(Long memberId, OrderRequest request) throws Exception;

    OrderDetailResponseDto getOrder(Long memberId, Long orderId);

    OrderListResponseDto getOrders(Long memberId);

    void cancelOrder(Long memberId, Long orderLineId);
}
