package com.member.memberapi.controller;


import com.member.memberapi.controller.req.EmailRequest;
import com.member.membercore.application.service.EmailUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailUseCase emailUseCase;

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequest request) {
        emailUseCase.sendEmail(request.email());
        return "Email sent";
    }

    @PostMapping("/verify")
    public String verifyEmail(@RequestBody EmailRequest request) {
        boolean flag = emailUseCase.verifyEmail(request.mapToCommand());
        return flag ? "Email verified" : "Email not verified";
    }

}
