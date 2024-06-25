package com.memberservice.controller;


import com.memberservice.controller.req.EmailRequest;
import com.memberservice.usecase.impl.EmailService;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequest request)
        throws MessagingException, UnsupportedEncodingException {
        emailService.sendEmail(request.email());
        return "Email sent";
    }

    @PostMapping("/verify")
    public String verifyEmail(@RequestBody EmailRequest request) {
        boolean flag = emailService.verifyEmail(request.mapToCommand());
        return flag ? "Email verified" : "Email not verified";
    }

}
