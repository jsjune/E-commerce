package com.member.memberapi.usecase.dto;

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
