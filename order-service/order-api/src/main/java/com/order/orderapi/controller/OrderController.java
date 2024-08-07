package com.order.orderapi.controller;


import com.order.orderapi.controller.req.CartOrderRequestDto;
import com.order.orderapi.usecase.OrderReadUseCase;
import com.order.orderapi.common.Response;
import com.order.orderapi.controller.req.OrderRequestFromCart;
import com.order.orderapi.controller.req.OrderRequestFromProduct;
import com.order.orderapi.controller.req.ProductOrderRequestDto;
import com.order.orderapi.usecase.OrderWriteUseCase;
import com.order.orderapi.usecase.dto.OrderDetailResponseDto;
import com.order.orderapi.usecase.dto.OrderListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private final OrderWriteUseCase orderWriteUseCase;
    private final OrderReadUseCase orderReadUseCase;

    @PostMapping("/create/cart")
    public Response<OrderDetailResponseDto> getOrderFromCart(@RequestHeader("Member-Id")Long memberId,
        @RequestBody CartOrderRequestDto request) {
        OrderDetailResponseDto data = orderReadUseCase.getOrderFromCart(memberId,
            request.mapToCommand());
        return Response.success(HttpStatus.OK.value(), data);
    }

    @PostMapping("/create/product")
    public Response<OrderDetailResponseDto> getOrderFromProduct(@RequestHeader("Member-Id")Long memberId,
        @RequestBody ProductOrderRequestDto request) {
        OrderDetailResponseDto data = orderReadUseCase.getOrderFromProduct(memberId,
            request.mapToCommand());
        return Response.success(HttpStatus.OK.value(), data);
    }

    @PostMapping("/submit/product")
    public Response<Void> submitOrderFromProduct(@RequestHeader("Member-Id")Long memberId,
        @RequestBody OrderRequestFromProduct request) {
        orderWriteUseCase.submitOrderFromProduct(memberId, request.mapToCommand());
        return Response.success(HttpStatus.OK.value(), null);
    }

    @PostMapping("/submit/cart")
    public Response<Void> submitOrderFromCart(@RequestHeader("Member-Id")Long memberId,
        @RequestBody OrderRequestFromCart request) {
        orderWriteUseCase.submitOrderFromCart(memberId, request.mapToCommand());
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/{orderId}")
    public Response<OrderDetailResponseDto> getOrder(@RequestHeader("Member-Id")Long memberId,@PathVariable Long orderId) {
        OrderDetailResponseDto data = orderReadUseCase.getOrder(memberId, orderId);
        return Response.success(HttpStatus.OK.value(), data);
    }

    @GetMapping
    public Response<OrderListResponseDto> getOrders(@RequestHeader("Member-Id")Long memberId) {
        OrderListResponseDto data = orderReadUseCase.getOrders(memberId);
        return Response.success(HttpStatus.OK.value(), data);
    }

    @PostMapping("/{orderLineId}/cancel")
    public Response<Void> cancelOrder(@RequestHeader("Member-Id")Long memberId,@PathVariable Long orderLineId) {
        orderWriteUseCase.cancelOrder(memberId, orderLineId);
        return Response.success(HttpStatus.OK.value(), null);
    }
}
