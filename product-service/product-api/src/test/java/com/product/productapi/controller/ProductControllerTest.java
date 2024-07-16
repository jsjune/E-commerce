package com.product.productapi.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.product.productapi.controller.req.ProductRequestDto;
import com.product.productapi.testConfig.ControllerTestSupport;
import com.product.productapi.usecase.dto.ProductListDto;
import com.product.productapi.usecase.dto.ProductListResponseDto;
import com.product.productapi.usecase.dto.ProductResponseDto;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

class ProductControllerTest extends ControllerTestSupport {

    @DisplayName("상품 등록하기")
    @Test
    void create_product() throws Exception {
        // given
        Long memberId = 1L;
        ProductRequestDto request = ProductRequestDto.builder().build();
        doNothing().when(productWriteUseCase).createProduct(eq(memberId), any());

        // when // then
        mockMvc.perform(
            post("/products")
                .header("Member-Id", memberId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("상품 상세 조회")
    @Test
    void get_product() throws Exception {
        // given
        Long memberId = 1L;
        Long productId = 1L;
        ProductResponseDto response = ProductResponseDto.builder()
            .name("abc")
            .tags(Set.of("tag1", "tag2", "tag3"))
            .orgProductImages(List.of("image1", "image2"))
            .build();
        when(productReadUseCase.getProduct(productId)).thenReturn(response);

        // when then
        mockMvc.perform(
            get("/products/{productId}", productId)
                .header("Member-Id", memberId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.name").value("abc"))
            .andExpect(jsonPath("$.data.tags", hasSize(3)))
            .andExpect(jsonPath("$.data.orgProductImages", hasSize(2)));

    }

    @DisplayName("상품 검색하기")
    @Test
    void get_products() throws Exception {
        // given
        String type = "name";
        String keyword = "a";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        ProductListResponseDto response = ProductListResponseDto.builder()
            .products(List.of(ProductListDto.builder().name("abc").build()))
            .hasNext(false)
            .build();
        when(productReadUseCase.getProducts(type, keyword, pageable)).thenReturn(response);

        // when then
        mockMvc.perform(
                get("/products")
                    .param("type", type)
                    .param("keyword", keyword)
                    .param("page", String.valueOf(page))
                    .param("size", String.valueOf(size))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.products[0].name").value("abc"))
            .andExpect(jsonPath("$.data.hasNext").value(false));

    }
}
