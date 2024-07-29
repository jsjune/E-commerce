package com.member.memberapi.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.member.memberapi.controller.req.LoginRequestDto;
import com.member.memberapi.controller.req.PasswordRequestDto;
import com.member.memberapi.controller.req.SignupRequestDto;
import com.member.memberapi.controller.req.UserInfoRequestDto;
import com.member.memberapi.controller.req.UserValidationRequestDto;
import com.member.memberapi.testConfig.ControllerTestSupport;
import com.member.membercore.application.service.dto.LoginResponseDto;
import com.member.membercore.application.service.dto.MemberInfoResponseDto;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class MemberControllerTest extends ControllerTestSupport {

    @DisplayName("이메일 검증")
    @Test
    void mail_check() throws Exception {
        // given
        String email = "abc@naver.com";
        String username = "abc";
        UserValidationRequestDto request = new UserValidationRequestDto(email,
            username);
        when(authUseCase.mailCheck(email)).thenReturn(false);

        // when then
        mockMvc.perform(
                post("/auth/mailCheck")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(false));

    }

    @DisplayName("이름 검증")
    @Test
    void username_check() throws Exception {
        // given
        String email = "abc@naver.com";
        String username = "abc";
        UserValidationRequestDto request = new UserValidationRequestDto(email,
            username);
        when(authUseCase.usernameCheck(email)).thenReturn(false);

        // when then
        mockMvc.perform(
                post("/auth/mailCheck")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(false));

    }

    @DisplayName("회원 가입")
    @Test
    void signup() throws Exception {
        // given
        SignupRequestDto request = SignupRequestDto.builder().build();
        doNothing().when(authUseCase).signup(request.mapToCommand());

        // when then
        mockMvc.perform(
                post("/auth/signup")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200));

    }

    @DisplayName("로그인")
    @Test
    void login() throws Exception {
        // given
        String account = "abc";
        LoginRequestDto request = new LoginRequestDto(account, "1234");
        String token = UUID.randomUUID().toString();
        LoginResponseDto response = LoginResponseDto.builder()
            .username(account)
            .accessToken(token)
            .build();
        when(authUseCase.login(request.mapToCommand())).thenReturn(response);

        // when then
        mockMvc.perform(
                post("/auth/login")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(jsonPath("$.data.username").value(account))
            .andExpect(jsonPath("$.data.accessToken").value(token));

    }

    @DisplayName("비밀번호 변경")
    @Test
    void update_pw() throws Exception {
        // given
        Long memberId = 1L;
        PasswordRequestDto request = new PasswordRequestDto("1234", "qwer");
        when(authUseCase.updatePw(request.mapToCommand(), memberId)).thenReturn(true);

        // when then
        mockMvc.perform(
                post("/auth/pw")
                    .header("Member-Id", memberId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").value(true));

    }

    @DisplayName("회원 정보 조회")
    @Test
    void get_user_info() throws Exception {
        // given
        Long memberId = 1L;
        String username = "abc";
        String email = "abc@naver.com";
        MemberInfoResponseDto response = MemberInfoResponseDto.builder()
            .memberId(memberId)
            .username(username)
            .email(email)
            .build();
        when(authUseCase.getUserInfo(memberId)).thenReturn(response);

        // when then
        mockMvc.perform(
            get("/auth/users")
                .header("Member-Id", memberId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(jsonPath("$.data.memberId").value(memberId))
            .andExpect(jsonPath("$.data.username").value(username))
            .andExpect(jsonPath("$.data.email").value(email));

    }

    @DisplayName("회원 정보 수정")
    @Test
    void update_user_info() throws Exception {
        // given
        Long memberId = 1L;
        String username = "abc";
        UserInfoRequestDto request = UserInfoRequestDto.builder()
            .username(username)
            .build();
        MemberInfoResponseDto response = MemberInfoResponseDto.builder()
            .memberId(memberId)
            .username(username)
            .build();
        when(authUseCase.updateUserInfo(memberId, request.mapToCommand())).thenReturn(response);

        // when then
        mockMvc.perform(
            post("/auth/users")
                .header("Member-Id", memberId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.memberId").value(memberId))
            .andExpect(jsonPath("$.data.username").value(username));

    }
}
