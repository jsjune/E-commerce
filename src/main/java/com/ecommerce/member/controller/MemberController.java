package com.ecommerce.member.controller;

import com.ecommerce.common.Response;
import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.member.controller.req.LoginRequestDto;
import com.ecommerce.member.controller.req.PasswordRequestDto;
import com.ecommerce.member.controller.req.SignupRequestDto;
import com.ecommerce.member.controller.req.UserInfoRequestDto;
import com.ecommerce.member.controller.req.UserValidationRequestDto;
import com.ecommerce.member.controller.res.LoginResponseDto;
import com.ecommerce.member.controller.res.MemberInfoResponseDto;
import com.ecommerce.member.usecase.AuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {

    private final AuthUseCase authUseCase;

    @PostMapping("/mailCheck")
    public Response<Boolean> mailCheck(@RequestBody UserValidationRequestDto request) {
        return Response.success(HttpStatus.OK.value(), authUseCase.mailCheck(request.getEmail()));
    }

    @PostMapping("/usernameCheck")
    public Response<Boolean> usernameCheck(@RequestBody UserValidationRequestDto request) {
        return Response.success(HttpStatus.OK.value(),
            authUseCase.usernameCheck(request.getUsername()));
    }

    @PostMapping("/signup")
    public Response<Void> signup(@RequestBody SignupRequestDto request) throws Exception {
        authUseCase.signup(request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @PostMapping("/login")
    public Response<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return Response.success(HttpStatus.OK.value(), authUseCase.login(request));
    }

    @PostMapping("/pw")
    public Response<Boolean> updatePw(@RequestHeader("Member-Id")Long memberId,
        @RequestBody PasswordRequestDto request) {
        return Response.success(HttpStatus.OK.value(),
            authUseCase.updatePw(request.getCurrentPw(), request.getNewPw(),
                memberId)
        );
    }

    @GetMapping("/users")
    public Response<MemberInfoResponseDto> getUserInfo(@RequestHeader("Member-Id")Long memberId)
        throws Exception {
        return Response.success(HttpStatus.OK.value(),
            authUseCase.getUserInfo(memberId));
    }

    @PostMapping("/users")
    public Response<MemberInfoResponseDto> updateUserInfo(
        @RequestHeader("Member-Id")Long memberId, @RequestBody UserInfoRequestDto request)
        throws Exception {
        return Response.success(HttpStatus.OK.value(),
            authUseCase.updateUserInfo(memberId, request));

    }
}
