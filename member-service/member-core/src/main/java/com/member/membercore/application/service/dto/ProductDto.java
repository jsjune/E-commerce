package com.member.membercore.application.service.dto;

import lombok.Builder;

@Builder
public record ProductDto(Long productId, String productName, Long price, String thumbnailUrl) {

}
