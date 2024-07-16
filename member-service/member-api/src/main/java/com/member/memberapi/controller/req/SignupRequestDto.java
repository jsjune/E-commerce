package com.member.memberapi.controller.req;

import com.member.memberapi.usecase.dto.SignupDto;
import lombok.Builder;

@Builder
public record SignupRequestDto (
    String username,
    String phoneNumber,
    String email,
    String password,
    String role,
    String company
){
    public SignupDto mapToCommand() {
        return SignupDto.builder()
            .username(username())
            .phoneNumber(phoneNumber())
            .email(email())
            .password(password())
            .role(role())
            .company(company())
            .build();
    }
}
