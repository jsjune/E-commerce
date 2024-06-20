package com.ecommerce.member.controller.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String account;
    private String password;
}
