package com.ecommerce.member.controller.req;

import lombok.Getter;

@Getter
public class UserValidationRequestDto {
    private String email;
    private String username;
}
