package com.member.memberapi.controller.req;


import com.member.membercore.application.service.dto.PasswordDto;

public record PasswordRequestDto(
    String currentPw,
    String newPw
) {
    public PasswordDto mapToCommand() {
        return new PasswordDto(currentPw(), newPw());
    }
}
