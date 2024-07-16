package com.member.memberapi.usecase.dto;

import lombok.Builder;

@Builder
public record UserInfoDto(
    String username,
    String phoneNumber,
    String email,
    String company
) {

}
