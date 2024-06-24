package com.ecommerce.member.adapter;

import com.ecommerce.member.adapter.dto.MemberDto;
import com.ecommerce.member.usecase.AuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/auth")
public class MemberAdapterController {

    private final AuthUseCase authUseCase;

    @GetMapping("/users")
    public MemberDto getMemberInfo(@RequestHeader("Member-Id") Long memberId) throws Exception {
        return authUseCase.getMemberInfo(memberId);
    };
}
