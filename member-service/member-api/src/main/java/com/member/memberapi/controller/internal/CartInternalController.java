package com.member.memberapi.controller.internal;


import com.member.memberapi.usecase.InternalCartUseCase;
import com.member.memberapi.usecase.dto.CartDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/carts")
public class CartInternalController {
    private final InternalCartUseCase internalCartUseCase;

    @PostMapping("/member/{memberId}")
    public List<CartDto> getCartList(@PathVariable Long memberId, @RequestBody List<Long> cartIds){
        return internalCartUseCase.getCartList(memberId, cartIds);
    }

    @DeleteMapping("/member/{memberId}")
    public void clearCart(@PathVariable Long memberId, @RequestBody List<Long> cartIds){
        internalCartUseCase.clearCart(memberId, cartIds);
    }

}
