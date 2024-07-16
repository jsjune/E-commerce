package com.member.memberapi.usecase.dto;

public record PasswordDto(
    String currentPw,
    String newPw
) {

}
