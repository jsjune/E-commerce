package com.ecommerce.member.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.common.error.ErrorCode;
import com.ecommerce.common.error.GlobalException;
import com.ecommerce.member.controller.req.LoginRequest;
import com.ecommerce.member.controller.req.LoginRequest.LoginRequestBuilder;
import com.ecommerce.member.controller.req.SignupRequest;
import com.ecommerce.member.controller.res.LoginResponse;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.entity.UserRole;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.member.usecase.AuthUseCase;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    AuthUseCase authUseCase;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAllInBatch();
    }


    @DisplayName("존재하는 이름 회원가입 실패")
    @Test
    void exist_username_fail_signup() {
        // given
        String username = "가나다";
        Member member = Member.builder()
            .username(username)
            .build();
        memberRepository.save(member);
        SignupRequest request = SignupRequest.builder()
            .username(username)
            .build();

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> authUseCase.signup(request));
        assertEquals(ErrorCode.EXIST_MEMBER, exception.getErrorCode());
    }

    @DisplayName("존재하는 이메일 회원가입 실패")
    @Test
    void exist_email_fail_signup() {
        // given
        String email = "abc@naver.com";
        Member member = Member.builder()
            .email(email)
            .build();
        memberRepository.save(member);
        SignupRequest request = SignupRequest.builder()
            .email(email)
            .build();

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> authUseCase.signup(request));
        assertEquals(ErrorCode.EXIST_MEMBER, exception.getErrorCode());
    }

    @DisplayName("회원가입 성공")
    @Test
    void signup() {
        // given
        String email = "aaa@naver.com";
        SignupRequest request = SignupRequest.builder()
            .username("abc")
            .phoneNumber("010-1234-5678")
            .email(email)
            .password("1234")
            .role(UserRole.USER)
            .company(null)
            .build();

        // when
        authUseCase.signup(request);

        // then
        Optional<Member> findMember = memberRepository.findByEmail(email);
        assertTrue(findMember.isPresent());
        assertThat(findMember.get().getUsername()).isEqualTo("abc");
    }

    @DisplayName("존재하지 않는 회원 로그인 실패")
    @Test
    void not_exist_member_login_fail() {
        // given
        LoginRequestBuilder request = LoginRequest.builder()
            .account("aaa")
            .password("1234");

        // when then
        AuthenticationException exception = assertThrows(AuthenticationException.class,
            () -> authUseCase.login(request.build()));
        assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @DisplayName("비밀번호 틀려서 로그인 실패")
    @Test
    void password_miss_match_login_fail() {
        String username = "가나다";
        Member member = Member.builder()
            .username(username)
            .password(encoder.encode("1234"))
            .build();
        memberRepository.save(member);
        // given
        LoginRequestBuilder request = LoginRequest.builder()
            .account(username)
            .password("12345");

        // when then
        AuthenticationException exception = assertThrows(AuthenticationException.class,
            () -> authUseCase.login(request.build()));
        assertEquals("자격 증명에 실패하였습니다.", exception.getMessage());
    }

    @DisplayName("이름으로 로그인")
    @Test
    void username_login() {
        // given
        String username = "가나다";
        Member member = Member.builder()
            .username(username)
            .password(encoder.encode("1234"))
            .role(UserRole.USER)
            .build();
        memberRepository.save(member);
        LoginRequest request = LoginRequest.builder()
            .account(username)
            .password("1234")
            .build();

        // when
        LoginResponse response = authUseCase.login(request);

        // then
        assertThat(response.getAccessToken()).isNotNull();


    }

    @DisplayName("이메일로 로그인")
    @Test
    void test() {
        // given
        String email = "abc@naver.com";
        Member member = Member.builder()
            .email(email)
            .password(encoder.encode("1234"))
            .role(UserRole.USER)
            .build();
        memberRepository.save(member);
        LoginRequest request = LoginRequest.builder()
            .account(email)
            .password("1234")
            .build();

        // when
        LoginResponse response = authUseCase.login(request);

        // then
        assertThat(response.getAccessToken()).isNotNull();

    }
}
