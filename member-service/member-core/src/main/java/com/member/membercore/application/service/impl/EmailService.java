package com.member.membercore.application.service.impl;


import com.member.membercore.application.service.EmailUseCase;
import com.member.membercore.application.service.dto.EmailDto;
import com.member.membercore.application.service.dto.EmailEvent;
import com.member.membercore.application.utils.EmailValidator;
import com.member.membercore.application.utils.RedisUtils;
import com.member.membercore.config.common.error.ErrorCode;
import com.member.membercore.config.common.error.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailUseCase {

    private final RedisUtils redisUtils;
    private final ApplicationEventPublisher eventPublisher;

    public void sendEmail(String toEmail) {
        boolean validate = EmailValidator.validate(toEmail);
        if(!validate) {
            throw new GlobalException(ErrorCode.INVALID_EMAIL_FORMAT);
        }

        if (redisUtils.existKey(toEmail)) {
            redisUtils.deleteKey(toEmail);
        }
        eventPublisher.publishEvent(new EmailEvent(toEmail));
    }

    public boolean verifyEmail(EmailDto command) {
        String findCode = redisUtils.getCode(command.email());
        if (findCode == null) {
            return false;
        }
        return findCode.equals(command.verifyCode());
    }

}
