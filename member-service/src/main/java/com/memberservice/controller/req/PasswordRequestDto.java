package com.memberservice.controller.req;

import com.memberservice.usecase.dto.PasswordDto;

public record PasswordRequestDto(
    String currentPw,
    String newPw
) {
    public PasswordDto mapToCommand() {
        return new PasswordDto(currentPw(), newPw());
    }
}
