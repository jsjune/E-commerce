package com.member.memberapi.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.member.memberapi.controller.req.EmailRequest;
import com.member.memberapi.testConfig.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class EmailControllerTest extends ControllerTestSupport {

    @DisplayName("이메일에 인증번호 전송하기")
    @Test
    void send_email() throws Exception {
        // given
        EmailRequest request = new EmailRequest("abc@naver.com", null);
        doNothing().when(emailUseCase).sendEmail(request.email());

        // when then
        mockMvc.perform(
                post("/email/send")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("Email sent"));

    }

    @DisplayName("이메일 검증코드 확인하기")
    @Test
    void test() throws Exception {
        // given
        EmailRequest request = new EmailRequest("abc@naver.com", "qwer");
        when(emailUseCase.verifyEmail(request.mapToCommand())).thenReturn(true);

        // when then
        mockMvc.perform(
                post("/email/verify")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("Email verified"));

    }

}
