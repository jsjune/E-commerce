package com.ecommerce.config.filter;

import static com.ecommerce.config.jwt.JwtProperties.HEADER_STRING;

import com.ecommerce.config.jwt.JwtUtils;
import com.ecommerce.member.auth.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtils.parseJwtToken(request.getHeader(HEADER_STRING));

        if (token != null && jwtUtils.validationJwtToken(token, response)) {
            LoginUser loginuser = jwtUtils.verify(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginuser, null, loginuser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
