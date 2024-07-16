package com.member.memberapi.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.common.cache.CachingCartListDto;
import com.member.memberapi.testConfig.ControllerTestSupport;
import com.member.memberapi.usecase.dto.CartResponseDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CartControllerTest extends ControllerTestSupport {

    @DisplayName("장바구니에 상품 추가하기")
    @Test
    void add_cart() throws Exception {
        // given
        Long productId = 1L;
        Long memberId = 1L;
        doNothing().when(cartUseCase).addCart(memberId, productId);

        // when then
        mockMvc.perform(
            post("/carts/add/{productId}", productId)
                .header("Member-Id", memberId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("장바구니 비우기")
    @Test
    void delete_cart() throws Exception {
        // given
        Long cartId = 1L;
        Long memberId = 1L;
        doNothing().when(cartUseCase).deleteCart(memberId, cartId);

        // when then
        mockMvc.perform(
            delete("/carts/{cartId}", cartId)
                .header("Member-Id", memberId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200));

    }

    @DisplayName("장바구니 목록 조회하기")
    @Test
    void get_cart_list() throws Exception {
        // given
        Long memberId = 1L;
        String productName = "상품";
        long price = 1000L;
        long quantity = 3L;
        CachingCartListDto cachingCartListDto = CachingCartListDto.builder()
            .productName(productName)
            .price(price)
            .quantity(quantity)
            .build();
        CartResponseDto response = new CartResponseDto(List.of(cachingCartListDto));
        when(cartUseCase.getCartList(memberId)).thenReturn(response);

        // when then
        mockMvc.perform(
                get("/carts")
                    .header("Member-Id", memberId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.carts[0].productName").value(productName))
            .andExpect(jsonPath("$.data.carts[0].price").value(price))
            .andExpect(jsonPath("$.data.carts[0].quantity").value(quantity));

    }

    @DisplayName("장바구니 수량 조절하기")
    @Test
    void update_cart_quantity() throws Exception {
        // given
        Long memberId = 1L;
        Long cartId = 1L;
        Long quantity = 3L;
        doNothing().when(cartUseCase).updateCartQuantity(memberId, cartId, quantity);

        // when then
        mockMvc.perform(
                post("/carts/{cartId}", cartId)
                    .header("Member-Id", memberId)
                    .param("quantity", quantity.toString())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200));

    }
}
