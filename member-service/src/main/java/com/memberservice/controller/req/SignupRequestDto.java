package com.memberservice.controller.req;

import com.memberservice.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignupRequestDto {
    private String username;
    private String phoneNumber;
    private String email;
    private String password;
    private UserRole role;
    private String company;
}
