package com.member.memberapi.usecase.dto;

public record MemberDto(
    Long memberId,
    String phoneNumber,
    String company
) {

}
