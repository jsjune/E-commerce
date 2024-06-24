package com.orderservice.adapter.dto;

public record MemberDto(
    Long memberId,
    String phoneNumber,
    String company
) {

}
