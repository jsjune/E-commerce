package com.member.memberapi.controller;


import com.member.memberapi.common.Response;
import com.member.memberapi.controller.req.LoginRequestDto;
import com.member.memberapi.controller.req.PasswordRequestDto;
import com.member.memberapi.controller.req.SignupRequestDto;
import com.member.memberapi.controller.req.UserInfoRequestDto;
import com.member.memberapi.controller.req.UserValidationRequestDto;
import com.member.memberapi.usecase.AuthUseCase;
import com.member.memberapi.usecase.dto.LoginResponseDto;
import com.member.memberapi.usecase.dto.MemberInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return Response.success(HttpStatus.OK.value(), authUseCase.mailCheck(request.email()));
    }

    @PostMapping("/usernameCheck")
    public Response<Boolean> usernameCheck(@RequestBody UserValidationRequestDto request) {
        return Response.success(HttpStatus.OK.value(),
            authUseCase.usernameCheck(request.username()));
    }

    @PostMapping("/signup")
    public Response<Void> signup(@RequestBody SignupRequestDto request) throws Exception {
        authUseCase.signup(request.mapToCommand());
        return Response.success(HttpStatus.OK.value(), null);
    }

    @PostMapping("/login")
    public Response<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return Response.success(HttpStatus.OK.value(), authUseCase.login(request.mapToCommand()));
    }

    @PostMapping("/pw")
    public Response<Boolean> updatePw(@RequestHeader("Member-Id")Long memberId,
        @RequestBody PasswordRequestDto request) {
        return Response.success(HttpStatus.OK.value(),
            authUseCase.updatePw(request.mapToCommand(), memberId)
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
            authUseCase.updateUserInfo(memberId, request.mapToCommand()));

    }
}
