package com.memberservice.controller.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PasswordRequestDto {
    private String currentPw;
    private String newPw;
}
