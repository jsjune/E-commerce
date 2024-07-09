package com.productservice.usecase.dto;

public record MemberDto(
    Long memberId,
    String phoneNumber,
    String company
) {

}
