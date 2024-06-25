package com.memberservice.controller.req;

import com.memberservice.usecase.dto.EmailDto;

public record EmailRequest(
    String email,
    String verifyCode
) {
    public EmailDto mapToCommand() {
        return new EmailDto(email(), verifyCode());
    }
}
