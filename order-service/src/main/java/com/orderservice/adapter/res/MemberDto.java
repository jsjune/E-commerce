package com.orderservice.adapter.res;

public record MemberDto(
    Long memberId,
    String phoneNumber,
    String company
) {

}
