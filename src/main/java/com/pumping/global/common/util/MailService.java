package com.pumping.global.common.util;

import com.pumping.domain.emailverification.model.EmailVerification;
import com.pumping.domain.emailverification.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MailService {

    private static final String MAIL_SUBJECT = "[PUMPING] 이메일 인증 코드";
    private static final Integer CODE_DURATION = 300;
    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender javaMailSender;

    @Transactional
    public void sendCodeEmail(String code, String email) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        String mailText = String.format("인증 코드: %s\n앱으로 돌아가서 인증을 완료해주세요", code);

        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(MAIL_SUBJECT);
        simpleMailMessage.setText(mailText);

        try {
            javaMailSender.send(simpleMailMessage);
        } catch (MailException e) {
            throw new RuntimeException(e);
        }


        EmailVerification emailVerification = new EmailVerification(email, code, LocalDateTime.now().plus(Duration.ofSeconds(CODE_DURATION)));

        emailVerificationRepository.save(emailVerification);

    }


}
