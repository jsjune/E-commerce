package com.memberservice.usecase;


import com.memberservice.adapter.dto.MemberDto;
import com.memberservice.controller.req.LoginRequestDto;
import com.memberservice.controller.req.SignupRequestDto;
import com.memberservice.controller.req.UserInfoRequestDto;
import com.memberservice.controller.res.LoginResponseDto;
import com.memberservice.controller.res.MemberInfoResponseDto;

public interface AuthUseCase {
    void signup(SignupRequestDto request) throws Exception;
    LoginResponseDto login(LoginRequestDto request);

    Boolean mailCheck(String email);

    Boolean usernameCheck(String username);

    boolean updatePw(String currentPw, String newPw, Long memberId);

    MemberInfoResponseDto getUserInfo(Long memberId) throws Exception;

    MemberInfoResponseDto updateUserInfo(Long memberId, UserInfoRequestDto request)
        throws Exception;

    MemberDto getMemberInfo(Long memberId) throws Exception;
}
