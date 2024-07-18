package com.member.membercore.application.service.impl;

import com.member.membercore.application.service.dto.EmailEvent;
import com.member.membercore.application.service.dto.SignupEmailEvent;
import com.member.membercore.application.utils.EmailValidator;
import com.member.membercore.application.utils.RedisUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

//@Component
@RequiredArgsConstructor
public class EmailListener {

    @Value("${spring.mail.username}")
    private String sender;

    private final RedisUtils redisUtils;
    private final JavaMailSender javaMailSender;

    @Async
    @EventListener
    public void listenEmail(EmailEvent event)
        throws MessagingException, UnsupportedEncodingException {
        MimeMessage emailForm = createEmailFormForCreateCode(event.toEmail());
        javaMailSender.send(emailForm);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listenSignUp(SignupEmailEvent event)
        throws MessagingException, UnsupportedEncodingException {
        boolean validate = EmailValidator.validate(event.email());
        if (validate) {
            MimeMessage emailForm = createEmailFormForSignUp(event.email());
            javaMailSender.send(emailForm);
        }
    }

    private MimeMessage createEmailFormForSignUp(String email)
        throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("회원가입 축하합니다!");

        StringBuilder msg = new StringBuilder();
        msg.append(
            "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">회원가입 완료</h1>");
        msg.append(
            "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">안녕하세요! 회원가입이 성공적으로 완료되었습니다.</p>");
        msg.append(
            "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">이제 다양한 서비스를 이용하실 수 있습니다. 저희와 함께 해주셔서 감사합니다!</p>");
        msg.append(
            "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><p style=\"font-size: 17px;\">회원가입을 축하드립니다! :)</p></div>");

        message.setText(msg.toString(), "utf-8", "html");
        message.setFrom(new InternetAddress(sender, "admin"));

        return message;
    }

    private MimeMessage createEmailFormForCreateCode(String email)
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

    private String createCode() {
        StringBuilder code = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(rnd.nextInt(10));
        }
        return code.toString();
    }

}
