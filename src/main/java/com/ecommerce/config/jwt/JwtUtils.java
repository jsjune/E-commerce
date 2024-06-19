package com.ecommerce.config.jwt;

import static com.ecommerce.common.error.ErrorCode.EXPIRED_ACCESS_TOKEN;
import static com.ecommerce.common.error.ErrorCode.INVALID_JWT_TOKEN;
import static com.ecommerce.common.error.ErrorCode.UNSUPPORTED_JWT_TOKEN;
import static com.ecommerce.config.jwt.JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME;
import static com.ecommerce.config.jwt.JwtProperties.TOKEN_PREFIX;

import com.ecommerce.common.Response;
import com.ecommerce.common.error.ErrorCode;
import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.entity.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class JwtUtils {

    private final SecretKey key;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtUtils(@Value("${jwt.app.jwtSecretKey}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessTokenFromLoginUser(LoginUser loginUser) {
        return Jwts.builder()
            .claim("id", loginUser.getMember().getId())
            .claim("username", loginUser.getMember().getUsername())
            .claim("role", loginUser.getMember().getRole().name())
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
            .signWith(key)
            .compact();
    }

    public String parseJwtToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
            token = token.replace(TOKEN_PREFIX, "");
        }
        return token;
    }

    public boolean validationJwtToken(String token, HttpServletResponse response)
        throws IOException {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            setResponse(response, INVALID_JWT_TOKEN, e);
        } catch (ExpiredJwtException e) {
            setResponse(response, EXPIRED_ACCESS_TOKEN, e);
        } catch (UnsupportedJwtException e) {
            setResponse(response, UNSUPPORTED_JWT_TOKEN, e);
        }
        return false;
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode, Exception e) throws IOException {
        log.error("error message {}", errorCode.getMessage(), e);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Response<String> errorResponse = Response.fail(errorCode.getHttpStatus().value(), errorCode.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    public LoginUser verify(String token) {
        Claims claims = extractClaims(token);
        Member member = buildMemberFromClaims(claims);
        return new LoginUser(member);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private Member buildMemberFromClaims(Claims claims) {
        return Member.builder()
            .id(claims.get("id", Long.class))
            .username(claims.get("username", String.class))
            .role(UserRole.valueOf(claims.get("role", String.class)))
            .build();
    }
}
