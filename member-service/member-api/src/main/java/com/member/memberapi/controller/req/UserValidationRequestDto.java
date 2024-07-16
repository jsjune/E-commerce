package com.member.memberapi.controller.req;

public record UserValidationRequestDto(
    String email,
    String username
) {
}
