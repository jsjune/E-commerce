package com.member.memberapi.controller.internal;


import com.member.memberapi.usecase.AuthUseCase;
import com.member.memberapi.usecase.dto.MemberDto;
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
