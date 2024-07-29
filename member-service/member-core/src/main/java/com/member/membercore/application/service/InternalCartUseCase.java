package com.member.membercore.application.service;

import com.member.membercore.application.service.dto.CartDto;
import java.util.List;

public interface InternalCartUseCase {
    List<CartDto> getCartList(Long memberId, List<Long> cartIds);

    void clearCart(Long memberId, List<Long> cartIds);
}
