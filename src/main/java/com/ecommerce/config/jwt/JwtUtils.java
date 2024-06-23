package com.ecommerce.config.jwt;

import static com.ecommerce.config.jwt.JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME;

import com.ecommerce.member.auth.LoginUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtils {

    private final SecretKey key;

    public JwtUtils(@Value("${jwt.app.jwtSecretKey}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessTokenFromLoginUser(LoginUser loginUser) {
        return Jwts.builder()
            .subject("access_token")
            .claim("id", loginUser.getMember().getId())
            .claim("role", loginUser.getMember().getRole().name())
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
            .signWith(key)
            .compact();
    }

}
