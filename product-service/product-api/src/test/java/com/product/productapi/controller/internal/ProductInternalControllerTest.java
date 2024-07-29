package com.product.productapi.controller.internal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.product.productapi.testConfig.ControllerTestSupport;
import com.product.productcore.application.service.dto.ProductDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ProductInternalControllerTest extends ControllerTestSupport {

    @DisplayName("서버간 내부 통신 - 상품 조회")
    @Test
    void find_product_by_id() throws Exception {
        // given
        Long productId = 1L;
        ProductDto productName = ProductDto.builder()
            .productId(productId)
            .productName("productName")
            .build();
        when(internalProductUseCase.findProductById(productId)).thenReturn(productName);

        // when // then
        mockMvc.perform(
                get("/internal/products/{productId}", productId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(productId))
            .andExpect(jsonPath("$.productName").value("productName"));

    }

    @DisplayName("서버간 내부 통신 - 재고 증가")
    @Test
    void increment_stock() throws Exception {
        // given
        Long productId = 1L;
        Long quantity = 1L;
        when(internalProductUseCase.incrementStock(productId, quantity)).thenReturn(true);

        // when // then
        mockMvc.perform(
                post("/internal/products/{productId}/increment", productId)
                    .param("quantity", quantity.toString())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(true));

    }
}
