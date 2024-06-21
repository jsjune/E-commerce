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
        return Response.success(HttpStatus.OK.value(),
            authUseCase.usernameCheck(request.getUsername()));
    }

    @PostMapping("/auth/signup")
    public Response<Void> signup(@RequestBody SignupRequestDto request) throws Exception {
        authUseCase.signup(request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @PostMapping("/auth/login")
    public Response<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return Response.success(HttpStatus.OK.value(), authUseCase.login(request));
    }

    @PostMapping("/auth/pw")
    public Response<Boolean> updatePw(@AuthenticationPrincipal LoginUser loginUser,
        @RequestBody PasswordRequestDto request) {
        return Response.success(HttpStatus.OK.value(),
            authUseCase.updatePw(request.getCurrentPw(), request.getNewPw(),
                loginUser.getMember().getId())
        );
    }

    @GetMapping("/auth/users")
    public Response<MemberInfoResponseDto> getUserInfo(@AuthenticationPrincipal LoginUser loginUser)
        throws Exception {
        return Response.success(HttpStatus.OK.value(),
            authUseCase.getUserInfo(loginUser.getMember().getId()));
    }

    @PostMapping("/auth/users")
    public Response<MemberInfoResponseDto> updateUserInfo(
        @AuthenticationPrincipal LoginUser loginUser, @RequestBody UserInfoRequestDto request)
        throws Exception {
        return Response.success(HttpStatus.OK.value(),
            authUseCase.updateUserInfo(loginUser.getMember().getId(), request));

    }
}
