package com.orderservice.adapter;

import com.orderservice.adapter.res.CartDto;
import com.orderservice.adapter.res.MemberDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "member-service")
public interface MemberClient {
    @GetMapping("/internal/auth/users")
    MemberDto getMemberInfo(@RequestHeader("Member-Id") Long memberId) throws Exception ;

    @PostMapping("/internal/carts/member/{memberId}")
    List<CartDto> getCartList(@PathVariable Long memberId, @RequestBody List<Long> cartIds);

    @DeleteMapping("/internal/carts/member/{memberId}")
    void clearCart(@PathVariable Long memberId,@RequestBody List<Long> cartIds);
}
