package com.memberservice.usecase;


import com.memberservice.usecase.dto.MemberDto;
import com.memberservice.usecase.dto.LoginResponseDto;
import com.memberservice.usecase.dto.MemberInfoResponseDto;
import com.memberservice.usecase.dto.LoginDto;
import com.memberservice.usecase.dto.PasswordDto;
import com.memberservice.usecase.dto.SignupDto;
import com.memberservice.usecase.dto.UserInfoDto;

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
