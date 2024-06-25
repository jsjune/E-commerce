package com.memberservice.controller.req;

import com.memberservice.usecase.dto.LoginDto;

public record LoginRequestDto (
    String account,
    String password
){
    public LoginDto mapToCommand() {
        return new LoginDto(account(), password());
    }
}
