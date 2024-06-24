package com.memberservice.controller.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberInfoResponseDto {
    private Long memberId;
    private String username;
    private String phoneNumber;
    private String email;
    private String company;
}
