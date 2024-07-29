package com.member.memberapi.controller.internal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.member.memberapi.testConfig.ControllerTestSupport;
import com.member.membercore.application.service.dto.MemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class MemberInternalControllerTest extends ControllerTestSupport {

    @DisplayName("서버간 통신 - 회원 정보 조회")
    @Test
    void get_member_info() throws Exception {
        // given
        Long memberId = 1L;
        String phoneNumber = "010-1234-5678";
        String company = "company";
        MemberDto response = new MemberDto(memberId, phoneNumber, company);
        when(authUseCase.getMemberInfo(memberId)).thenReturn(response);

        // when then
        mockMvc.perform(
                get("/internal/auth/users")
                    .header("Member-Id", memberId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(jsonPath("$.memberId").value(memberId))
            .andExpect(jsonPath("$.phoneNumber").value(phoneNumber))
            .andExpect(jsonPath("$.company").value(company));

    }
}
