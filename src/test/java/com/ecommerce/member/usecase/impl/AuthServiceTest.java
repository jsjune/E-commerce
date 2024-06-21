package com.ecommerce.member.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.common.error.ErrorCode;
import com.ecommerce.common.error.GlobalException;
import com.ecommerce.member.controller.req.LoginRequestDto;
import com.ecommerce.member.controller.req.SignupRequestDto;
import com.ecommerce.member.controller.res.LoginResponseDto;
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

    @DisplayName("유저 계정 중복 검사 존재할때")
    @Test
    void exists_username() {
        // given
        String username = "abc";
        Member member = Member.builder()
            .username(username)
            .build();
        memberRepository.save(member);

        // when
        Boolean result = authUseCase.usernameCheck(username);

        // then
        assertEquals(result, false);
    }

    @DisplayName("유저 계정 중복 검사 존재하지 않을때")
    @Test
    void not_exists_username() {
        // given
        String username = "abc";

        // when
        Boolean result = authUseCase.usernameCheck(username);

        // then
        assertEquals(result, true);
    }

    @DisplayName("이메일 중복 검사 존재할때")
    @Test
    void exists_email() {
        // given
        String email = "abc@naver.com";
        Member member = Member.builder()
            .email(email)
            .build();
        memberRepository.save(member);

        // when
        Boolean result = authUseCase.mailCheck(email);

        // then
        assertEquals(result, false);

    }

    @DisplayName("이메일 중복 검사 존재하지 않을때")
    @Test
    void email_check_not_exists_email() {
        // given
        String email = "abc@naver.com";

        // when
        Boolean result = authUseCase.mailCheck(email);

        // then
        assertEquals(result, true);
    }

    @DisplayName("이메일 중복 검사 존재하지 않을때")
    @Test
    void email_check_invalid_email_format() {
        // given
        String email = "abc";

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> authUseCase.mailCheck(email));
        assertEquals(ErrorCode.INVALID_EMAIL_FORMAT, exception.getErrorCode());
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
        SignupRequestDto request = SignupRequestDto.builder()
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
        SignupRequestDto request = SignupRequestDto.builder()
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
        SignupRequestDto request = SignupRequestDto.builder()
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
        LoginRequestDto request = LoginRequestDto.builder()
            .account("aaa")
            .password("1234")
            .build();

        // when then
        AuthenticationException exception = assertThrows(AuthenticationException.class,
            () -> authUseCase.login(request));
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
        LoginRequestDto request = LoginRequestDto.builder()
            .account(username)
            .password("12345")
            .build();

        // when then
        AuthenticationException exception = assertThrows(AuthenticationException.class,
            () -> authUseCase.login(request));
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
        LoginRequestDto request = LoginRequestDto.builder()
            .account(username)
            .password("1234")
            .build();

        // when
        LoginResponseDto response = authUseCase.login(request);

        // then
        assertThat(response.getAccessToken()).isNotNull();


    }

    @DisplayName("이메일로 로그인")
    @Test
    void email_login() {
        // given
        String email = "abc@naver.com";
        Member member = Member.builder()
            .email(email)
            .password(encoder.encode("1234"))
            .role(UserRole.USER)
            .build();
        memberRepository.save(member);
        LoginRequestDto request = LoginRequestDto.builder()
            .account(email)
            .password("1234")
            .build();

        // when
        LoginResponseDto response = authUseCase.login(request);

        // then
        assertThat(response.getAccessToken()).isNotNull();

    }
}
