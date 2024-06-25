package com.memberservice.usecase.dto;

public record PasswordDto(
    String currentPw,
    String newPw
) {

}
