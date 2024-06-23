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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping("/cart/create")
    public Response<OrderDetailResponseDto> registerOrderOfCart(@RequestHeader("Member-Id")Long memberId,
        @RequestBody CartOrderRequestDto request) {
        OrderDetailResponseDto data = orderUseCase.registerOrderOfCart(memberId,
            request.getCartIds());
        return Response.success(HttpStatus.OK.value(), data);
    }

    @PostMapping("/create")
    public Response<OrderDetailResponseDto> registerOrder(@RequestHeader("Member-Id")Long memberId,@RequestBody
        ProductOrderRequestDto request) {
        OrderDetailResponseDto data = orderUseCase.registerOrder(memberId,
            request);
        return Response.success(HttpStatus.OK.value(), data);
    }

    @PostMapping("/submit")
    public Response<Void> submitOrder(@RequestHeader("Member-Id")Long memberId, @RequestBody
        OrderRequest request) throws Exception {
        orderUseCase.submitOrder(memberId, request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/{orderId}")
    public Response<OrderDetailResponseDto> getOrder(@RequestHeader("Member-Id")Long memberId,@PathVariable Long orderId) {
        OrderDetailResponseDto data = orderUseCase.getOrder(memberId, orderId);
        return Response.success(HttpStatus.OK.value(), data);
    }

    @GetMapping
    public Response<OrderListResponseDto> getOrders(@RequestHeader("Member-Id")Long memberId) {
        OrderListResponseDto data = orderUseCase.getOrders(memberId);
        return Response.success(HttpStatus.OK.value(), data);
    }

    @PostMapping("/{orderLineId}/cancel")
    public Response<Void> cancelOrder(@RequestHeader("Member-Id")Long memberId,@PathVariable Long orderLineId) {
        orderUseCase.cancelOrder(memberId, orderLineId);
        return Response.success(HttpStatus.OK.value(), null);
    }
}
