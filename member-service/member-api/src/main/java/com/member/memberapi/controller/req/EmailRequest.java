package com.member.memberapi.controller.req;


import com.member.membercore.application.service.dto.EmailDto;

public record EmailRequest(
    String email,
    String verifyCode
) {
    public EmailDto mapToCommand() {
        return new EmailDto(email(), verifyCode());
    }
}
