package com.memberservice.usecase.dto;

public record EmailDto(
    String email,
    String verifyCode
) {

}
