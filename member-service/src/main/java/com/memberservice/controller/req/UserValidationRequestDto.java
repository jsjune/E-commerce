package com.memberservice.controller.req;

public record UserValidationRequestDto(
    String email,
    String username
) {
}
