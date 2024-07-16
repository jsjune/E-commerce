package com.member.memberapi.controller.req;

import com.member.memberapi.usecase.dto.EmailDto;

public record EmailRequest(
    String email,
    String verifyCode
) {
    public EmailDto mapToCommand() {
        return new EmailDto(email(), verifyCode());
    }
}
