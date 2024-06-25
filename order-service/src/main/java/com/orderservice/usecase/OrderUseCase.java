package com.orderservice.usecase;


import com.orderservice.controller.req.OrderRequest;
import com.orderservice.controller.req.ProductOrderRequestDto;
import com.orderservice.controller.res.OrderDetailResponseDto;
import com.orderservice.controller.res.OrderListResponseDto;
import com.orderservice.usecase.dto.OrderDto;
import com.orderservice.usecase.dto.RegisterOrderOfCartDto;
import com.orderservice.usecase.dto.RegisterOrderOfProductDto;
import java.util.List;

public interface OrderUseCase {

    OrderDetailResponseDto registerOrderOfCart(Long memberId, RegisterOrderOfCartDto command);

    OrderDetailResponseDto registerOrder(Long memberId, RegisterOrderOfProductDto command);

    void submitOrder(Long memberId, OrderDto command) throws Exception;

    OrderDetailResponseDto getOrder(Long memberId, Long orderId);

    OrderListResponseDto getOrders(Long memberId);

    void cancelOrder(Long memberId, Long orderLineId);
}
