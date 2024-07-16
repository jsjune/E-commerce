package com.member.memberapi.controller.req;

import com.member.memberapi.usecase.dto.PasswordDto;

public record PasswordRequestDto(
    String currentPw,
    String newPw
) {
    public PasswordDto mapToCommand() {
        return new PasswordDto(currentPw(), newPw());
    }
}
