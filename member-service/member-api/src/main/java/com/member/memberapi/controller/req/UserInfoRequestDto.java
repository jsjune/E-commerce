package com.member.memberapi.controller.req;

import com.member.membercore.application.service.dto.UserInfoDto;
import lombok.Builder;

@Builder
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
