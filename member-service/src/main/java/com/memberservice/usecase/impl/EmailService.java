package com.memberservice.usecase.impl;


import com.memberservice.usecase.dto.EmailDto;
import com.memberservice.usecase.dto.EmailEvent;
import com.memberservice.utils.EmailValidator;
import com.memberservice.utils.RedisUtils;
import com.memberservice.utils.error.ErrorCode;
import com.memberservice.utils.error.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

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
