package com.memberservice.usecase.dto;

import lombok.Builder;

@Builder
public record MemberInfoResponseDto(
    Long memberId,
    String username,
    String phoneNumber,
    String email,
    String company
) {
}
