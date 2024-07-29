package com.member.membercore.application.service;


import com.member.membercore.application.service.dto.LoginDto;
import com.member.membercore.application.service.dto.LoginResponseDto;
import com.member.membercore.application.service.dto.MemberDto;
import com.member.membercore.application.service.dto.MemberInfoResponseDto;
import com.member.membercore.application.service.dto.PasswordDto;
import com.member.membercore.application.service.dto.SignupDto;
import com.member.membercore.application.service.dto.UserInfoDto;

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
