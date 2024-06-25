package com.memberservice.controller.req;

import com.memberservice.entity.UserRole;
import com.memberservice.usecase.dto.SignupDto;

public record SignupRequestDto (
    String username,
    String phoneNumber,
    String email,
    String password,
    UserRole role,
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
