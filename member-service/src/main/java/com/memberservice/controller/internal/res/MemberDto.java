package com.memberservice.controller.internal.res;

public record MemberDto(
    Long memberId,
    String phoneNumber,
    String company
) {

}
