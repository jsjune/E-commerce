package com.delivery.deliveryapi.Controller;

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

import com.delivery.deliveryapi.Controller.req.AddressRequestDto;
import com.delivery.deliveryapi.testConfig.ControllerTestSupport;
import com.delivery.deliverycore.application.service.dto.DeliveryAddressListDto;
import com.delivery.deliverycore.application.service.dto.DeliveryAddressListResponseDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class DeliveryControllerTest extends ControllerTestSupport {

    @DisplayName("주소 등록 테스트")
    @Test
    void register_address() throws Exception {
        // given
        Long memberId = 1L;
        AddressRequestDto request = AddressRequestDto.builder().build();
        doNothing().when(deliveryAddressUseCase).registerAddress(eq(memberId), any());

        // when // then
        mockMvc.perform(
            post("/delivery/addresses")
                .header("Member-Id", memberId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("등록한 배송지 주소 조회")
    @Test
    void get_addresses() throws Exception {
        // given
        Long memberId = 1L;
        DeliveryAddressListDto mainAddress = DeliveryAddressListDto.builder()
            .receiver("aaa")
            .mainAddress(true)
            .build();
        DeliveryAddressListDto address = DeliveryAddressListDto.builder()
            .receiver("bbb")
            .mainAddress(false)
            .build();
        DeliveryAddressListResponseDto response = new DeliveryAddressListResponseDto(
            List.of(mainAddress, address));
        when(deliveryAddressUseCase.getAddresses(memberId)).thenReturn(response);

        // when then
        mockMvc.perform(
                get("/delivery/addresses")
                    .header("Member-Id", memberId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.deliveryAddresses", hasSize(2)))
            .andExpect(jsonPath("$.data.deliveryAddresses[0].receiver").value("aaa"))
            .andExpect(jsonPath("$.data.deliveryAddresses[0].mainAddress").value(true))
            .andExpect(jsonPath("$.data.deliveryAddresses[1].receiver").value("bbb"))
            .andExpect(jsonPath("$.data.deliveryAddresses[1].mainAddress").value(false));



    }
}
