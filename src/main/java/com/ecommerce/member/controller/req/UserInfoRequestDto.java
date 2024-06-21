package com.ecommerce.member.controller.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserInfoRequestDto {
    private String username;
    private String phoneNumber;
    private String email;
    private String company;
}
