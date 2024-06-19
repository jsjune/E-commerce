package com.ecommerce.member.controller;

import com.ecommerce.common.Response;
import com.ecommerce.member.controller.req.LoginRequest;
import com.ecommerce.member.controller.req.SignupRequest;
import com.ecommerce.member.controller.res.LoginResponse;
import com.ecommerce.member.usecase.AuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final AuthUseCase authUseCase;

    @PostMapping("/auth/signup")
    public Response<Void> signup(@RequestBody SignupRequest request){
        authUseCase.signup(request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @PostMapping("/auth/login")
    public Response<LoginResponse> login(@RequestBody LoginRequest request) {
        return Response.success(HttpStatus.OK.value(), authUseCase.login(request));
    }

}
