package com.order.orderapi.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.orderapi.controller.req.CartOrderRequestDto;
import com.order.orderapi.controller.req.OrderRequestFromCart;
import com.order.orderapi.controller.req.OrderRequestFromProduct;
import com.order.orderapi.controller.req.ProductOrderRequestDto;
import com.order.orderapi.testConfig.ControllerTestSupport;
import com.order.orderapi.usecase.dto.OrderDetailResponseDto;
import com.order.orderapi.usecase.dto.OrderLineDto;
import com.order.orderapi.usecase.dto.OrderListResponseDto;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class OrderControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("장바구니 주문서 작성")
    void getOrderFromCart() throws Exception {
        // given
        Long memberId = 1L;
        CartOrderRequestDto request = new CartOrderRequestDto(List.of(1L, 2L));
        String status = "COMPLETED";
        long totalPrice = 10000L;
        long totalDiscount = 0L;
        OrderDetailResponseDto response = OrderDetailResponseDto.builder()
            .orderLines(List.of(OrderLineDto.builder().build()))
            .orderStatus(status)
            .totalPrice(totalPrice)
            .totalDiscount(totalDiscount)
            .build();
        when(orderReadUseCase.getOrderFromCart(memberId, request.mapToCommand())).thenReturn(
            response);

        // when then
        mockMvc.perform(
                post("/order/create/cart")
                    .header("Member-Id", memberId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.orderStatus").value(status))
            .andExpect(jsonPath("$.data.totalPrice").value(totalPrice))
            .andExpect(jsonPath("$.data.totalDiscount").value(totalDiscount));
    }

    @Test
    @DisplayName("상품 주문서 작성")
    void getOrderFromProduct() throws Exception {
        // given
        Long memberId = 1L;
        ProductOrderRequestDto request = new ProductOrderRequestDto(1L, 3L);
        String status = "COMPLETED";
        long totalPrice = 10000L;
        long totalDiscount = 0L;
        OrderDetailResponseDto response = OrderDetailResponseDto.builder()
            .orderLines(List.of(OrderLineDto.builder().build()))
            .orderStatus(status)
            .totalPrice(totalPrice)
            .totalDiscount(totalDiscount)
            .build();
        when(orderReadUseCase.getOrderFromProduct(memberId, request.mapToCommand())).thenReturn(
            response);

        // when then
        mockMvc.perform(
                post("/order/create/product")
                    .header("Member-Id", memberId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.orderStatus").value(status))
            .andExpect(jsonPath("$.data.totalPrice").value(totalPrice))
            .andExpect(jsonPath("$.data.totalDiscount").value(totalDiscount));
    }

    @Test
    @DisplayName("상품 주문하기")
    void submitOrderFromProduct() throws Exception {
        // given
        Long memberId = 1L;
        OrderRequestFromProduct request = new OrderRequestFromProduct(1L, 1L, 1L, 1L);
        doNothing().when(orderWriteUseCase)
            .submitOrderFromProduct(memberId, request.mapToCommand());

        // when then
        mockMvc.perform(
                post("/order/submit/product")
                    .header("Member-Id", memberId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("장바구니 주문하기")
    void submitOrderFromCart() throws Exception {
        // given
        Long memberId = 1L;
        OrderRequestFromCart request = new OrderRequestFromCart(List.of(1L, 2L), 1L,
            1L);
        doNothing().when(orderWriteUseCase).submitOrderFromCart(memberId, request.mapToCommand());

        // when then
        mockMvc.perform(
                post("/order/submit/cart")
                    .header("Member-Id", memberId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("주문 상세 조회")
    void getOrder() throws Exception {
        // given
        Long orderId = 1L;
        Long memberId = 1L;
        OrderDetailResponseDto response = OrderDetailResponseDto.builder()
            .orderLines(List.of(OrderLineDto.builder().build()))
            .orderStatus("COMPLETED")
            .totalPrice(10000L)
            .totalDiscount(0L)
            .build();
        when(orderReadUseCase.getOrder(memberId, orderId)).thenReturn(response);

        // when then
        mockMvc.perform(
                get("/order/{orderId}", orderId)
                    .header("Member-Id", memberId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.orderLines", hasSize(1)))
            .andExpect(jsonPath("$.data.orderStatus").value("COMPLETED"))
            .andExpect(jsonPath("$.data.totalPrice").value(10000L))
            .andExpect(jsonPath("$.data.totalDiscount").value(0L));
    }

    @Test
    @DisplayName("주문 목록 조회")
    void getOrders() throws Exception {
        // given
        Long memberId = 1L;
        List<OrderDetailResponseDto> orderDetails = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            OrderDetailResponseDto orderDetail = OrderDetailResponseDto.builder()
                .orderLines(List.of(OrderLineDto.builder().build()))
                .orderStatus("COMPLETED")
                .totalPrice(10000L)
                .totalDiscount(0L)
                .build();
            orderDetails.add(orderDetail);
        }
        OrderListResponseDto response = new OrderListResponseDto(orderDetails);
        when(orderReadUseCase.getOrders(memberId)).thenReturn(response);

        // when then
        mockMvc.perform(
                get("/order")
                    .header("Member-Id", memberId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.orders", hasSize(3)));
    }

    @Test
    @DisplayName("주문 취소")
    void cancelOrder() throws Exception {
        // given
        Long memberId = 1L;
        Long orderLineId = 1L;
        doNothing().when(orderWriteUseCase).cancelOrder(memberId, orderLineId);

        // when then
        mockMvc.perform(
            post("/order/{orderLineId}/cancel", orderLineId)
                .header("Member-Id", memberId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200));
    }
}
