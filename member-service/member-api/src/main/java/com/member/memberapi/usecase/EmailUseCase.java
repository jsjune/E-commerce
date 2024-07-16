package com.member.memberapi.usecase;

import com.member.memberapi.usecase.dto.EmailDto;

public interface EmailUseCase {

    void sendEmail(String toEmail);

    boolean verifyEmail(EmailDto command);
}
