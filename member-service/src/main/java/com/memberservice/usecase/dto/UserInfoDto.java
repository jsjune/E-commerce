package com.memberservice.usecase.dto;

import lombok.Builder;

@Builder
public record UserInfoDto(
    String username,
    String phoneNumber,
    String email,
    String company
) {

}
