package com.memberservice.controller.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private String username;
    private String role;
    private String accessToken;
}
