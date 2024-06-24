package com.memberservice.usecase.impl;


import com.memberservice.adapter.dto.MemberDto;
import com.memberservice.auth.LoginUser;
import com.memberservice.config.jwt.JwtUtils;
import com.memberservice.controller.req.LoginRequestDto;
import com.memberservice.controller.req.SignupRequestDto;
import com.memberservice.controller.req.UserInfoRequestDto;
import com.memberservice.controller.res.LoginResponseDto;
import com.memberservice.controller.res.MemberInfoResponseDto;
import com.memberservice.entity.Member;
import com.memberservice.entity.UserRole;
import com.memberservice.repository.MemberRepository;
import com.memberservice.usecase.AuthUseCase;
import com.memberservice.utils.AesUtil;
import com.memberservice.utils.EmailValidator;
import com.memberservice.utils.error.ErrorCode;
import com.memberservice.utils.error.GlobalException;
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
                .memberId(member.getId())
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
                .memberId(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .phoneNumber(aesUtil.aesDecode(member.getPhoneNumber()))
                .company(member.getCompany())
                .build();
        }
        return null;
    }

    @Override
    public MemberDto getMemberInfo(Long memberId) throws Exception {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberDto(member.getId(), aesUtil.aesDecode(member.getPhoneNumber()),
            member.getCompany());
    }
}

