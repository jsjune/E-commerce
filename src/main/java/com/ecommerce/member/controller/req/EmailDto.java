package com.ecommerce.member.controller.req;

import lombok.Getter;

@Getter
public class EmailDto {
    private String email;
    private String verifyCode;
}
