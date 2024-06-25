package com.memberservice.usecase.impl;


import com.memberservice.usecase.dto.EmailDto;
import com.memberservice.utils.EmailValidator;
import com.memberservice.utils.RedisUtils;
import com.memberservice.utils.error.ErrorCode;
import com.memberservice.utils.error.GlobalException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String sender;

    private final JavaMailSender javaMailSender;
    private final RedisUtils redisUtils;

    public void sendEmail(String toEmail)
        throws MessagingException, UnsupportedEncodingException {
        boolean validate = EmailValidator.validate(toEmail);
        if(!validate) {
            throw new GlobalException(ErrorCode.INVALID_EMAIL_FORMAT);
        }

        if (redisUtils.existKey(toEmail)) {
            redisUtils.deleteKey(toEmail);
        }
        // 이메일 폼 생성
        MimeMessage emailForm = createEmailForm(toEmail);
        // 이메일 발송
        javaMailSender.send(emailForm);
    }

    private MimeMessage createEmailForm(String email)
        throws MessagingException, UnsupportedEncodingException {

        String authCode = createCode();
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("회원가입 인증 코드: ");

        StringBuilder msg = new StringBuilder();
        msg.append(
            "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 인증</h1>");
        msg.append(
            "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 회원가입 화면에서 입력해주세요.</p>");
        msg.append(
            "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">");
        msg.append(authCode);
        msg.append("</td></tr></tbody></table></div>");

        message.setText(msg.toString(), "utf-8", "html");
        message.setFrom(new InternetAddress(sender, "admin"));

        redisUtils.setData(email, authCode, 60 * 5L);
        return message;
    }

    public String createCode() {
        StringBuilder code = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(rnd.nextInt(10));
        }
        return code.toString();
    }

    public boolean verifyEmail(EmailDto command) {
        String findCode = redisUtils.getCode(command.email());
        if (findCode == null) {
            return false;
        }
        return findCode.equals(command.verifyCode());
    }

}
