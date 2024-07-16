package com.member.memberapi.controller.req;

import com.member.memberapi.usecase.dto.LoginDto;

public record LoginRequestDto (
    String account,
    String password
){
    public LoginDto mapToCommand() {
        return new LoginDto(account(), password());
    }
}
