package com.memberservice.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.memberservice.IntegrationTestSupport;
import com.memberservice.auth.LoginUser;
import com.memberservice.controller.req.LoginRequestDto;
import com.memberservice.controller.req.PasswordRequestDto;
import com.memberservice.controller.req.SignupRequestDto;
import com.memberservice.controller.req.UserInfoRequestDto;
import com.memberservice.controller.res.LoginResponseDto;
import com.memberservice.controller.res.MemberInfoResponseDto;
import com.memberservice.entity.Member;
import com.memberservice.entity.UserRole;
import com.memberservice.repository.MemberRepository;
import com.memberservice.usecase.AuthUseCase;
import com.memberservice.usecase.dto.LoginDto;
import com.memberservice.usecase.dto.PasswordDto;
import com.memberservice.usecase.dto.SignupDto;
import com.memberservice.usecase.dto.UserInfoDto;
import com.memberservice.utils.AesUtil;
import com.memberservice.utils.error.ErrorCode;
import com.memberservice.utils.error.GlobalException;
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
    @Autowired
    private AesUtil aesUtil;

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
        SignupDto command = SignupDto.builder()
            .username(username)
            .build();

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> authUseCase.signup(command));
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
        SignupDto command = SignupDto.builder()
            .email(email)
            .build();

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> authUseCase.signup(command));
        assertEquals(ErrorCode.EXIST_MEMBER, exception.getErrorCode());
    }

    @DisplayName("회원가입 성공")
    @Test
    void signup() throws Exception {
        // given
        String email = "aaa@naver.com";
        String username = "abc";
        SignupDto command = SignupDto.builder()
            .username(username)
            .phoneNumber("010-1234-5678")
            .email(email)
            .password("1234")
            .role(UserRole.USER)
            .company(null)
            .build();

        // when
        authUseCase.signup(command);

        // then
        Optional<Member> findMember = memberRepository.findByEmail(email);
        assertTrue(findMember.isPresent());
        assertEquals(findMember.get().getEmail(), email);
        assertEquals(findMember.get().getUsername(), username);
    }

    @DisplayName("존재하지 않는 회원 로그인 실패")
    @Test
    void not_exist_member_login_fail() {
        // given
        LoginDto command = new LoginDto("aaa", "1234");

        // when then
        AuthenticationException exception = assertThrows(AuthenticationException.class,
            () -> authUseCase.login(command));
        assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @DisplayName("비밀번호 틀려서 로그인 실패")
    @Test
    void password_miss_match_login_fail() {
        String username = "가나다";
        Member member = Member.builder()
            .username(username)
            .password(encoder.encode("234"))
            .build();
        memberRepository.save(member);
        // given
        LoginDto command = new LoginDto(username, "1234");

        // when then
        AuthenticationException exception = assertThrows(AuthenticationException.class,
            () -> authUseCase.login(command));
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
        LoginDto command = new LoginDto(username, "1234");

        // when
        LoginResponseDto response = authUseCase.login(command);

        // then
        assertNotNull(response.getAccessToken());
        assertEquals(response.getUsername(), username);

    }

    @DisplayName("이메일로 로그인")
    @Test
    void email_login() {
        // given
        String email = "abc@naver.com";
        String username = "abc";
        Member member = Member.builder()
            .email(email)
            .username(username)
            .password(encoder.encode("1234"))
            .role(UserRole.USER)
            .build();
        memberRepository.save(member);
        LoginDto command = new LoginDto(email, "1234");

        // when
        LoginResponseDto response = authUseCase.login(command);

        // then
        assertNotNull(response.getAccessToken());
        assertEquals(response.getUsername(), username);
    }

    @DisplayName("잘못된 비밀번호로 수정 실패")
    @Test
    void password_update_fail() {
        // given
        String currentPw = "1234";
        Member member = Member.builder()
            .password(encoder.encode(currentPw))
            .build();
        memberRepository.save(member);
        PasswordDto command = new PasswordDto("12345", "123456");

        // when
        boolean result = authUseCase.updatePw(command, member.getId());

        // then
        assertFalse(result);
    }

    @DisplayName("비밀번호 수정 성공")
    @Test
    void password_update_success() {
        // given
        String currentPw = "1234";
        Member member = Member.builder()
            .password(encoder.encode(currentPw))
            .build();
        memberRepository.save(member);
        PasswordDto command = new PasswordDto(currentPw, "123456");

        // when
        boolean result = authUseCase.updatePw(command, member.getId());

        // then
        assertTrue(result);

    }

    @DisplayName("내 프로필 조회")
    @Test
    void get_user_info() throws Exception {
        // given
        String phoneNumber = "010-1234-5678";
        Member member = Member.builder()
            .email("abc@naver.com")
            .username("abc")
            .phoneNumber(aesUtil.aesEncode(phoneNumber))
            .role(UserRole.USER)
            .company(null)
            .build();
        memberRepository.save(member);
        LoginUser loginUser = new LoginUser(member);

        // when
        MemberInfoResponseDto result = authUseCase.getUserInfo(loginUser.getMember().getId());

        // then
        assertEquals(result.getEmail(), member.getEmail());
        assertEquals(result.getPhoneNumber(), phoneNumber);
    }

    @DisplayName("프로필 수정")
    @Test
    void test() throws Exception {
        // given
        Member member = Member.builder()
            .email("abc@naver.com")
            .username("abc")
            .phoneNumber(aesUtil.aesEncode("010-1234-5678"))
            .role(UserRole.USER)
            .company(null)
            .build();
        memberRepository.save(member);
        String username = "aaa";
        UserInfoDto command = UserInfoDto.builder()
            .email("abc@naver.com")
            .username(username)
            .phoneNumber("010-1234-5678")
            .company(null)
            .build();
        LoginUser loginUser = new LoginUser(member);

        // when
        MemberInfoResponseDto result = authUseCase.updateUserInfo(
            loginUser.getMember().getId(), command);

        // then
        assertEquals(result.getUsername(), username);

    }
}
