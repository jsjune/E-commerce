package com.memberservice.auth;


import com.memberservice.entity.Member;
import com.memberservice.repository.MemberRepository;
import com.memberservice.utils.error.ErrorCode;
import com.memberservice.utils.error.GlobalException;
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
