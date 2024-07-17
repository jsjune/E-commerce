package com.order.ordercore.application.service.dto;

public record MemberDto(
    Long memberId,
    String phoneNumber,
    String company
) {

}
