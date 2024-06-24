package com.memberservice.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.memberservice.utils.Response;
import com.memberservice.utils.error.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        if (authException.getClass().equals(BadCredentialsException.class)) {
            setResponse(response, ErrorCode.BAD_CREDENTIALS, authException);
        }

    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode, AuthenticationException ex) throws IOException {
        log.error("error message {}", errorCode.getMessage(), ex);
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Response<String> fail = Response.fail(errorCode.getHttpStatus().value(), errorCode.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(fail));
    }
}
