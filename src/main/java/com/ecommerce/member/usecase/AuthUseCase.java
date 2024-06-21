package com.ecommerce.member.usecase;

import com.ecommerce.member.controller.req.LoginRequestDto;
import com.ecommerce.member.controller.req.SignupRequestDto;
import com.ecommerce.member.controller.res.LoginResponseDto;

public interface AuthUseCase {
    void signup(SignupRequestDto request);
    LoginResponseDto login(LoginRequestDto request);

    Boolean mailCheck(String email);

    Boolean usernameCheck(String username);
}
