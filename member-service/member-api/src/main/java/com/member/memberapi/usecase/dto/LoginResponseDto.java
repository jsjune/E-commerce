package com.member.memberapi.usecase.dto;

import lombok.Builder;

@Builder
public record LoginResponseDto(
    Long userId,
    String username,
    String role,
    String accessToken
) {
}
