package com.ecommerce.member.controller;

import com.ecommerce.member.controller.req.EmailDto;
import com.ecommerce.member.usecase.impl.EmailService;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@RequestParam EmailDto emailDto)
        throws MessagingException, UnsupportedEncodingException {
        emailService.sendEmail(emailDto.getEmail());
        return "Email sent";
    }

    @PostMapping("/verify")
    public String verifyEmail(@RequestBody EmailDto emailDto) {
        boolean flag = emailService.verifyEmail(emailDto.getEmail(), emailDto.getVerifyCode());
        return flag ? "Email verified" : "Email not verified";
    }

}
