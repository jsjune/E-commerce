package com.ecommerce.member.usecase.impl;

import com.ecommerce.common.AesUtil;
import com.ecommerce.common.error.ErrorCode;
import com.ecommerce.common.error.GlobalException;
import com.ecommerce.config.jwt.JwtUtils;
import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.member.controller.req.LoginRequestDto;
import com.ecommerce.member.controller.req.SignupRequestDto;
import com.ecommerce.member.controller.req.UserInfoRequestDto;
import com.ecommerce.member.controller.res.LoginResponseDto;
import com.ecommerce.member.controller.res.MemberInfoResponseDto;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.entity.UserRole;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.member.usecase.AuthUseCase;
import com.ecommerce.member.utils.EmailValidator;
import java.util.Optional;
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
    private final AesUtil aesUtil;

    @Override
    public void signup(SignupRequestDto request) throws Exception {
        memberRepository.findByEmailOrUsername(request.getEmail(), request.getUsername())
            .ifPresent(member -> {
                throw new GlobalException(ErrorCode.EXIST_MEMBER);
            });
        Member member = Member.builder()
            .username(request.getUsername())
            .phoneNumber(aesUtil.aesEncode(request.getPhoneNumber()))
            .email(request.getEmail())
            .password(bCryptPasswordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .company(request.getCompany())
            .build();
        memberRepository.save(member);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            request.getAccount(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessTokenFromLoginUser(loginUser);
        return new LoginResponseDto(
            loginUser.getMember().getId(),
            loginUser.getMember().getUsername(),
            loginUser.getMember().getRole().name(),
            accessToken
        );
    }

    @Override
    public Boolean mailCheck(String email) {
        boolean validate = EmailValidator.validate(email);
        if (!validate) {
            throw new GlobalException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
        return memberRepository.findByEmail(email).isEmpty();
    }

    @Override
    public Boolean usernameCheck(String username) {
        return memberRepository.findByUsername(username).isEmpty();
    }

    @Override
    public boolean updatePw(String currentPw, String newPw, Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Member m = findMember.get();
            boolean matches = bCryptPasswordEncoder.matches(currentPw, m.getPassword());
            if (matches) {
                m.updatePassword(bCryptPasswordEncoder.encode(newPw));
                memberRepository.save(m);
                return true;
            }
        }
        return false;
    }

    @Override
    public MemberInfoResponseDto getUserInfo(Long memberId) throws Exception {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            return MemberInfoResponseDto.builder()
                    .username(member.getUsername())
                    .email(member.getEmail())
                    .phoneNumber(aesUtil.aesDecode(member.getPhoneNumber()))
                    .company(member.getCompany())
                    .build();
        }
        return null;
    }

    @Override
    public MemberInfoResponseDto updateUserInfo(Long memberId, UserInfoRequestDto request)
        throws Exception {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            member.updateInfo(request.getUsername(), aesUtil.aesEncode(request.getPhoneNumber()),
                request.getEmail(), request.getCompany());
            memberRepository.save(member);
            return MemberInfoResponseDto.builder()
                .username(member.getUsername())
                .email(member.getEmail())
                .phoneNumber(aesUtil.aesDecode(member.getPhoneNumber()))
                .company(member.getCompany())
                .build();
        }
        return null;
    }

    @Override
    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    public Member findByIdAndRole(Long memberId) {
        return memberRepository.findByIdAndRole(memberId, UserRole.SELLER)
            .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
    }
}

