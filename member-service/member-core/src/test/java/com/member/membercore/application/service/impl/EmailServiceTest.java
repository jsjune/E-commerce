package com.member.membercore.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.member.membercore.IntegrationTestSupport;
import com.member.membercore.application.service.dto.EmailDto;
import com.member.membercore.application.service.dto.EmailEvent;
import com.member.membercore.application.utils.RedisUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class EmailServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationEvents events;
    @Autowired
    private EmailService emailService;
    @MockBean
    private RedisUtils redisUtils;

    @DisplayName("검증 코드 이메일 보내기")
    @Test
    void send_email() {
        // given
        String email = "abc@naver.com";

        // when
        emailService.sendEmail(email);
        long count = events.stream(EmailEvent.class).count();

        // then
        assertEquals(count, 1);

    }

    @DisplayName("인증 코드로 이메일 검증 성공")
    @Test
    void verify_email() {
        // given
        String code = "1234";
        EmailDto emailDto = new EmailDto("abc@naver.com", code);

        // when
        when(redisUtils.getCode(emailDto.email())).thenReturn(code);
        boolean result = emailService.verifyEmail(emailDto);

        // then
        assertTrue(result);
    }
}
