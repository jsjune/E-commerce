package com.member.membercore.application.auth;


import com.member.membercore.config.common.error.ErrorCode;
import com.member.membercore.config.common.error.GlobalException;
import com.member.membercore.infrastructure.entity.Member;
import com.member.membercore.infrastructure.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmailOrUsername(account, account)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        return new LoginUser(member);
    }

}
