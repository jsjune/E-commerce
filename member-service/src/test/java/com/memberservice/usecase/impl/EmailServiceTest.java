package com.memberservice.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.memberservice.IntegrationTestSupport;
import com.memberservice.usecase.dto.EmailEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class EmailServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationEvents events;
    @Autowired
    private EmailService emailService;

    @DisplayName("검증 코드 이메일 보내기")
    @Test
    void test() {
        // given
        String email = "abc@naver.com";

        // when
        emailService.sendEmail(email);

        // then
        long count = events.stream(EmailEvent.class).count();
        assertEquals(count, 1);

    }
}
