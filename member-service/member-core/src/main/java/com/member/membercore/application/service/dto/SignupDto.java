package com.member.membercore.application.service.dto;

import lombok.Builder;

@Builder
public record SignupDto(
    String username,
    String phoneNumber,
    String email,
    String password,
    String role,
    String company
) {

}
