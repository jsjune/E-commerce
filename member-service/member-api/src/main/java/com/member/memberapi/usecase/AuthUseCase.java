package com.member.memberapi.usecase;


import com.member.memberapi.usecase.dto.LoginDto;
import com.member.memberapi.usecase.dto.LoginResponseDto;
import com.member.memberapi.usecase.dto.MemberDto;
import com.member.memberapi.usecase.dto.MemberInfoResponseDto;
import com.member.memberapi.usecase.dto.PasswordDto;
import com.member.memberapi.usecase.dto.SignupDto;
import com.member.memberapi.usecase.dto.UserInfoDto;

public interface AuthUseCase {
    void signup(SignupDto command) throws Exception;

    LoginResponseDto login(LoginDto command);

    Boolean mailCheck(String email);

    Boolean usernameCheck(String username);

    boolean updatePw(PasswordDto command, Long memberId);

    MemberInfoResponseDto getUserInfo(Long memberId) throws Exception;

    MemberInfoResponseDto updateUserInfo(Long memberId, UserInfoDto command)
        throws Exception;

    MemberDto getMemberInfo(Long memberId) throws Exception;
}
