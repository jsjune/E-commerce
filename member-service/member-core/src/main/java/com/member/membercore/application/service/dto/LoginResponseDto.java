package com.member.membercore.application.service.dto;

import lombok.Builder;

@Builder
public record LoginResponseDto(
    Long userId,
    String username,
    String role,
    String accessToken
) {
}
