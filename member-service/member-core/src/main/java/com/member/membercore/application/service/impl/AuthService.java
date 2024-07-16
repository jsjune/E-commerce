package com.member.membercore.application.service.impl;


import com.member.memberapi.common.error.ErrorCode;
import com.member.memberapi.common.error.GlobalException;
import com.member.memberapi.usecase.AuthUseCase;
import com.member.memberapi.usecase.dto.LoginDto;
import com.member.memberapi.usecase.dto.LoginResponseDto;
import com.member.memberapi.usecase.dto.MemberDto;
import com.member.memberapi.usecase.dto.MemberInfoResponseDto;
import com.member.memberapi.usecase.dto.PasswordDto;
import com.member.memberapi.usecase.dto.SignupDto;
import com.member.memberapi.usecase.dto.UserInfoDto;
import com.member.membercore.application.auth.LoginUser;
import com.member.membercore.application.service.dto.SignupEmailEvent;
import com.member.membercore.application.utils.AesUtil;
import com.member.membercore.application.utils.EmailValidator;
import com.member.membercore.config.jwt.JwtUtils;
import com.member.membercore.infrastructure.entity.Member;
import com.member.membercore.infrastructure.entity.UserRole;
import com.member.membercore.infrastructure.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void signup(SignupDto command) throws Exception {
        memberRepository.findByEmailOrUsername(command.email(), command.username())
            .ifPresent(member -> {
                throw new GlobalException(ErrorCode.EXIST_MEMBER);
            });
        Member member = Member.builder()
            .username(command.username())
            .phoneNumber(aesUtil.aesEncode(command.phoneNumber()))
            .email(command.email())
            .password(bCryptPasswordEncoder.encode(command.password()))
            .role(UserRole.valueOf(command.role()))
            .company(command.company())
            .build();
        memberRepository.save(member);
        eventPublisher.publishEvent(new SignupEmailEvent(command.email()));
    }

    @Override
    public LoginResponseDto login(LoginDto command) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            command.account(), command.password());
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
    public boolean updatePw(PasswordDto command, Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Member m = findMember.get();
            boolean matches = bCryptPasswordEncoder.matches(command.currentPw(), m.getPassword());
            if (matches) {
                m.updatePassword(bCryptPasswordEncoder.encode(command.newPw()));
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
    public MemberInfoResponseDto updateUserInfo(Long memberId, UserInfoDto command)
        throws Exception {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            member.updateInfo(command.username(), aesUtil.aesEncode(command.phoneNumber()),
                command.email(), command.company());
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
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            return new MemberDto(member.getId(), aesUtil.aesDecode(member.getPhoneNumber()),
                member.getCompany());
        }
        return null;
    }
}

