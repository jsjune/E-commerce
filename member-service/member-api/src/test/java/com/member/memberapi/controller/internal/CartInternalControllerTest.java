package com.member.memberapi.controller.internal;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.member.memberapi.testConfig.ControllerTestSupport;
import com.member.memberapi.usecase.dto.CartDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CartInternalControllerTest extends ControllerTestSupport {

    @DisplayName("서버간 통신 - 장바구니 정보 조회")
    @Test
    void get_cart_list() throws Exception {
        // given
        Long memberId = 1L;
        List<Long> cartIds = List.of(1L, 2L, 3L);
        long productId = 1L;
        String productName = "상품";
        long price = 1000L;
        String thumbnailUrl = "썸네일1";
        long quantity = 3L;
        List<CartDto> response = List.of(
            new CartDto(productId, productName, price, thumbnailUrl, quantity),
            new CartDto(2L, "상품2", 2000L, "썸네일2", 3L),
            new CartDto(3L, "상품3", 3000L, "썸네일3", 3L)
        );
        when(internalCartUseCase.getCartList(memberId, cartIds)).thenReturn(response);

        // when then
        mockMvc.perform(
                post("/internal/carts/member/{memberId}", memberId)
                    .content(objectMapper.writeValueAsString(cartIds))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(jsonPath("$[0].productId").value(productId))
            .andExpect(jsonPath("$[0].productName").value(productName))
            .andExpect(jsonPath("$[0].price").value(price))
            .andExpect(jsonPath("$[0].thumbnailUrl").value(thumbnailUrl))
            .andExpect(jsonPath("$[0].quantity").value(quantity))
            .andExpect(jsonPath("$", hasSize(3)));

    }

    @DisplayName("서버간 통신 - 장바구니 비우기")
    @Test
    void clear_cart() throws Exception {
        // given
        Long memberId = 1L;
        List<Long> cartIds = List.of(1L, 2L, 3L);
        doNothing().when(internalCartUseCase).clearCart(memberId, cartIds);

        // when then
        mockMvc.perform(
                delete("/internal/carts/member/{memberId}", memberId)
                    .content(objectMapper.writeValueAsString(cartIds))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());

    }
}
