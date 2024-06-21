package com.ecommerce.member.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailValidatorTest {
    @DisplayName("이메일 검증 실패")
    @Test
    void email_validate_fail() {
        // given
        String email = "abc";

        // when
        boolean result = EmailValidator.validate(email);

        // then
        assertEquals(result, false);
    }

    @DisplayName("이메일 검증 통과")
    @Test
    void email_validate_success() {
        // given
        String email = "abc@naver.com";

        // when
        boolean result = EmailValidator.validate(email);

        // then
        assertEquals(result, true);
    }
}
