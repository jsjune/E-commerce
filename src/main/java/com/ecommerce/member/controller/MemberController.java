package com.ecommerce.member.controller;

import com.ecommerce.common.Response;
import com.ecommerce.member.controller.req.LoginRequestDto;
import com.ecommerce.member.controller.req.SignupRequestDto;
import com.ecommerce.member.controller.req.UserValidationRequestDto;
import com.ecommerce.member.controller.res.LoginResponseDto;
import com.ecommerce.member.usecase.AuthUseCase;
import com.ecommerce.member.utils.EmailValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final AuthUseCase authUseCase;

    @PostMapping("/auth/mailCheck")
    public Response<Boolean> mailCheck(@RequestBody UserValidationRequestDto request) {
        return Response.success(HttpStatus.OK.value(), authUseCase.mailCheck(request.getEmail()));
    }

    @PostMapping("/auth/usernameCheck")
    public Response<Boolean> usernameCheck(@RequestBody UserValidationRequestDto request) {
        return Response.success(HttpStatus.OK.value(), authUseCase.usernameCheck(request.getUsername()));
    }

    @PostMapping("/auth/signup")
    public Response<Void> signup(@RequestBody SignupRequestDto request){
        authUseCase.signup(request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @PostMapping("/auth/login")
    public Response<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return Response.success(HttpStatus.OK.value(), authUseCase.login(request));
    }

}
