package com.memberservice.usecase.dto;

public record MemberDto(
    Long memberId,
    String phoneNumber,
    String company
) {

}
