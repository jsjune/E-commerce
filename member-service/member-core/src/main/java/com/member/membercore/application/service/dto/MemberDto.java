package com.member.membercore.application.service.dto;

public record MemberDto(
    Long memberId,
    String phoneNumber,
    String company
) {

}
