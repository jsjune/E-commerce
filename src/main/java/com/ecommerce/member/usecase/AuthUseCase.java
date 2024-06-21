package com.ecommerce.member.usecase;

import com.ecommerce.member.controller.req.LoginRequestDto;
import com.ecommerce.member.controller.req.SignupRequestDto;
import com.ecommerce.member.controller.req.UserInfoRequestDto;
import com.ecommerce.member.controller.res.LoginResponseDto;
import com.ecommerce.member.controller.res.MemberInfoResponseDto;

public interface AuthUseCase {
    void signup(SignupRequestDto request) throws Exception;
    LoginResponseDto login(LoginRequestDto request);

    Boolean mailCheck(String email);

    Boolean usernameCheck(String username);

    boolean updatePw(String currentPw, String newPw, Long memberId);

    MemberInfoResponseDto getUserInfo(Long memberId) throws Exception;

    MemberInfoResponseDto updateUserInfo(Long memberId, UserInfoRequestDto request)
        throws Exception;
}
