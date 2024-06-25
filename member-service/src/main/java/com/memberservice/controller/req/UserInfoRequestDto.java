package com.memberservice.controller.req;

import com.memberservice.usecase.dto.UserInfoDto;

public record UserInfoRequestDto(
    String username,
    String phoneNumber,
    String email,
    String company
) {
    public UserInfoDto mapToCommand() {
        return UserInfoDto.builder()
            .username(username())
            .phoneNumber(phoneNumber())
            .email(email())
            .company(company())
            .build();
    }
}
