package com.memberservice.usecase.dto;

import com.memberservice.entity.UserRole;
import lombok.Builder;

@Builder
public record SignupDto(
    String username,
    String phoneNumber,
    String email,
    String password,
    UserRole role,
    String company
) {

}
