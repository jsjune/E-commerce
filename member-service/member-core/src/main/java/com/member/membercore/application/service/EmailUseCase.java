package com.member.membercore.application.service;


import com.member.membercore.application.service.dto.EmailDto;

public interface EmailUseCase {

    void sendEmail(String toEmail);

    boolean verifyEmail(EmailDto command);
}
