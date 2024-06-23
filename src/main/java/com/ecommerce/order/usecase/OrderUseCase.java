package com.ecommerce.order.usecase;

import com.ecommerce.order.controller.req.OrderRequest;
import com.ecommerce.order.controller.req.ProductOrderRequestDto;
import com.ecommerce.order.controller.res.OrderDetailResponseDto;
import com.ecommerce.order.controller.res.OrderListResponseDto;
import java.util.List;

public interface OrderUseCase {

    void registerOrderOfCart(Long memberId, List<Long> cartIds);

    void registerOrder(Long memberId, ProductOrderRequestDto request);

    void submitOrder(Long memberId, OrderRequest request) throws Exception;

    OrderDetailResponseDto getOrder(Long memberId, Long orderId);

    OrderListResponseDto getOrders(Long memberId);

    void cancelOrder(Long memberId, Long orderLineId);
}
