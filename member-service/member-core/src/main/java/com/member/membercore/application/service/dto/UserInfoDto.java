package com.member.membercore.application.service.dto;

import lombok.Builder;

@Builder
public record UserInfoDto(
    String username,
    String phoneNumber,
    String email,
    String company
) {

}
