package com.member.memberapi.usecase;

import com.member.memberapi.usecase.dto.CartDto;
import java.util.List;

public interface InternalCartUseCase {
    List<CartDto> getCartList(Long memberId, List<Long> cartIds);

    void clearCart(Long memberId, List<Long> cartIds);
}
