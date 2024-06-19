package com.ecommerce.member.controller.req;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String account;
    private String password;
}
