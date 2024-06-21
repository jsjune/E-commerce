package com.ecommerce.member.controller.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberInfoResponseDto {
    private String username;
    private String phoneNumber;
    private String email;
    private String company;
}
