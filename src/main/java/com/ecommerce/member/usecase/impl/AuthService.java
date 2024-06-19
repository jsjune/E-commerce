package com.ecommerce.member.usecase.impl;

import com.ecommerce.common.error.ErrorCode;
import com.ecommerce.common.error.GlobalException;
import com.ecommerce.config.jwt.JwtUtils;
import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.member.controller.req.LoginRequest;
import com.ecommerce.member.controller.req.SignupRequest;
import com.ecommerce.member.controller.res.LoginResponse;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.member.usecase.AuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements AuthUseCase {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public void signup(SignupRequest request) {
        memberRepository.findByEmailOrUsername(request.getEmail(), request.getUsername())
            .ifPresent(member -> {
                throw new GlobalException(ErrorCode.EXIST_MEMBER);
            });
        Member member = Member.builder()
            .username(request.getUsername())
            .phoneNumber(request.getPhoneNumber())
            .email(request.getEmail())
            .password(bCryptPasswordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .company(request.getCompany())
            .build();
        memberRepository.save(member);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            request.getAccount(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessTokenFromLoginUser(loginUser);
        return new LoginResponse(
            loginUser.getMember().getId(),
            loginUser.getMember().getUsername(),
            loginUser.getMember().getRole().name(),
            accessToken
        );
    }
}
