package com.memberservice.usecase.dto;

public record LoginResponseDto(
    Long userId,
    String username,
    String role,
    String accessToken
) {
}
