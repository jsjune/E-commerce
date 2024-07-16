package com.delivery.deliveryapi.Controller.internal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delivery.deliveryapi.testConfig.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class DeliveryInternalControllerTest extends ControllerTestSupport {

    @DisplayName("배송 상태 확인 테스트")
    @Test
    void delivery_status_check() throws Exception {
        // given
        Long deliveryId = 1L;
        Boolean expectedStatus = true;
        when(internalDeliveryUseCase.deliveryStatusCheck(deliveryId)).thenReturn(expectedStatus);

        // when then
        mockMvc.perform(
                get("/internal/delivery/{deliveryId}/status", deliveryId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(expectedStatus.toString()));

    }
}
