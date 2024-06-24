package com.memberservice.adapter;


import com.memberservice.adapter.dto.CartDto;
import com.memberservice.usecase.CartUseCase;
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
public class CartAdapterController {
    private final CartUseCase cartUseCase;

    @PostMapping("/member/{memberId}")
    public List<CartDto> getCartList(@PathVariable Long memberId, @RequestBody List<Long> cartIds){
        return cartUseCase.getCartList(memberId, cartIds);
    }

    @DeleteMapping("/member/{memberId}")
    public void clearCart(@PathVariable Long memberId, @RequestBody List<Long> productIds){
        cartUseCase.clearCart(memberId, productIds);
    }

}
