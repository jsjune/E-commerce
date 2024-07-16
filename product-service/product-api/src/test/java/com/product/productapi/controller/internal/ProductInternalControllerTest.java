package com.product.productapi.controller.internal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.product.productapi.testConfig.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ProductInternalControllerTest extends ControllerTestSupport {

    @DisplayName("")
    @Test
    void test() throws Exception {
        // given
        Long productId = 1L;

        // when // then
        mockMvc.perform(
            get("/internal/products/{productId}", productId)
                .contentType(MediaType.APPLICATION_JSON)
        )

    }
}
