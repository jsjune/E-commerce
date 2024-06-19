package com.ecommerce.member.usecase;

import com.ecommerce.member.controller.req.LoginRequest;
import com.ecommerce.member.controller.req.SignupRequest;
import com.ecommerce.member.controller.res.LoginResponse;

public interface AuthUseCase {
    void signup(SignupRequest request);
    LoginResponse login(LoginRequest request);
}
