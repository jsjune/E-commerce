package com.ecommerce.member.adapter.dto;

public record MemberDto(
    Long memberId,
    String phoneNumber,
    String company
) {

}
