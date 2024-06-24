package com.productservice.adapter;

import com.productservice.adapter.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "memberClient", url = "${memberClient.url}")
public interface MemberClient {
    @GetMapping("/internal/auth/users")
    MemberDto getMemberInfo(@RequestHeader("Member-Id") Long memberId) throws Exception ;
}
