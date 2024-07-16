package com.member.memberapi.usecase.dto;

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
