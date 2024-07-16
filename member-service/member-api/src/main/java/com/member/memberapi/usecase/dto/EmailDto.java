package com.member.memberapi.usecase.dto;

public record EmailDto(
    String email,
    String verifyCode
) {

}
