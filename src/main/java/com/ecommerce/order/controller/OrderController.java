package com.ecommerce.order.controller;

import com.ecommerce.common.Response;
import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.order.controller.req.CartOrderRequestDto;
import com.ecommerce.order.controller.req.OrderRequest;
import com.ecommerce.order.controller.req.ProductOrderRequestDto;
import com.ecommerce.order.controller.res.OrderDetailResponseDto;
import com.ecommerce.order.controller.res.OrderListResponseDto;
import com.ecommerce.order.usecase.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping("/order/cart/create")
    public Response<Void> registerOrderOfCart(@AuthenticationPrincipal LoginUser loginUser,
        @RequestBody CartOrderRequestDto request) {
        orderUseCase.registerOrderOfCart(loginUser.getMember().getId(), request.getCartIds());
        return Response.success(HttpStatus.OK.value(), null);
    }

    @PostMapping("/order/create")
    public Response<Void> registerOrder(@AuthenticationPrincipal LoginUser loginUser,@RequestBody
        ProductOrderRequestDto request) {
        orderUseCase.registerOrder(loginUser.getMember().getId(), request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @PostMapping("/order/submit")
    public Response<Void> submitOrder(@AuthenticationPrincipal LoginUser loginUser, @RequestBody
        OrderRequest request) throws Exception {
        orderUseCase.submitOrder(loginUser.getMember().getId(), request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/order/{orderId}")
    public Response<OrderDetailResponseDto> getOrder(@AuthenticationPrincipal LoginUser loginUser, Long orderId) {
        OrderDetailResponseDto data = orderUseCase.getOrder(loginUser.getMember().getId(), orderId);
        return Response.success(HttpStatus.OK.value(), data);
    }

    @GetMapping("/order")
    public Response<OrderListResponseDto> getOrders(@AuthenticationPrincipal LoginUser loginUser) {
        OrderListResponseDto data = orderUseCase.getOrders(loginUser.getMember().getId());
        return Response.success(HttpStatus.OK.value(), data);
    }

    @PostMapping("/orderLine/{orderLineId}/cancel")
    public Response<Void> cancelOrder(@AuthenticationPrincipal LoginUser loginUser,@PathVariable Long orderLineId) {
        orderUseCase.cancelOrder(loginUser.getMember().getId(), orderLineId);
        return Response.success(HttpStatus.OK.value(), null);
    }
}
