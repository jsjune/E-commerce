package com.ecommerce.member.controller.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginRequestDto {
    private String account;
    private String password;
}
