package com.payment.paymentapi.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.payment.paymentapi.controller.req.PaymentMethodRequestDto;
import com.payment.paymentapi.testConfig.ControllerTestSupport;
import com.payment.paymentcore.application.service.dto.PaymentMethodListDto;
import com.payment.paymentcore.application.service.dto.PaymentMethodResponseDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class PaymentControllerTest extends ControllerTestSupport {

    @DisplayName("결제 수단 등록하기")
    @Test
    void register_paymen_method() throws Exception {
        // given
        Long memberId = 1L;
        PaymentMethodRequestDto request = PaymentMethodRequestDto.builder().build();
        doNothing().when(paymentMethodUseCase).registerPaymentMethod(eq(memberId), eq(request.mapToCommand()));

        // when // then
        mockMvc.perform(
            post("/payment/methods")
                .header("Member-Id", memberId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200));

    }

    @DisplayName("결제 수단 조회하기")
    @Test
    void test() throws Exception {
        // given
        Long memberId = 1L;
        PaymentMethodResponseDto response = new PaymentMethodResponseDto(
            List.of(
                PaymentMethodListDto.builder().build(),
                PaymentMethodListDto.builder().build(),
                PaymentMethodListDto.builder().build()
            )
        );
        when(paymentMethodUseCase.getPaymentMethods(memberId)).thenReturn(response);

        // when then
        mockMvc.perform(
            get("/payment/methods")
                .header("Member-Id", memberId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.paymentMethods", hasSize(3)));

    }
}
