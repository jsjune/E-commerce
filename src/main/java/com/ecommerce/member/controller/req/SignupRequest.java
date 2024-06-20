package com.ecommerce.member.controller.req;

import com.ecommerce.member.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignupRequest {
    private String username;
    private String phoneNumber;
    private String email;
    private String password;
    private UserRole role;
    private String company;
}
