package com.memberservice.controller.internal;


import com.memberservice.controller.internal.res.MemberDto;
import com.memberservice.usecase.AuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/auth")
public class MemberInternalController {

    private final AuthUseCase authUseCase;

    @GetMapping("/users")
    public MemberDto getMemberInfo(@RequestHeader("Member-Id") Long memberId) throws Exception {
        return authUseCase.getMemberInfo(memberId);
    };
}
