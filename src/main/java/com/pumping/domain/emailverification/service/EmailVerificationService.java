package com.pumping.domain.emailverification.service;

import com.pumping.domain.emailverification.exception.CodeVerificationException;
import com.pumping.domain.emailverification.model.EmailVerification;
import com.pumping.domain.emailverification.repository.EmailVerificationRepository;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;
import com.pumping.global.common.util.RandomCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final String MAIL_SUBJECT = "[PUMPING] 이메일 인증 코드";
    private static final Integer CODE_DURATION = 300;
    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender javaMailSender;

    @Transactional
    public void sendCode(String email) {

        String code = RandomCodeGenerator.generateCode();

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

        Optional<EmailVerification> optionalEmailVerification = emailVerificationRepository.findByEmail(email);
        LocalDateTime time = LocalDateTime.now().plus(Duration.ofSeconds(CODE_DURATION));

        if (optionalEmailVerification.isEmpty()) {
            EmailVerification emailVerification = new EmailVerification(email, code, time);
            emailVerificationRepository.save(emailVerification);
        } else {
            EmailVerification emailVerification = optionalEmailVerification.get();
            emailVerification.updateCode(code);
            emailVerification.updateExpiredAt(time);
        }


    }


    @Transactional(readOnly = true)
    public void checkCode(EmailCodeCheckRequest emailCodeCheckRequest) {

        EmailVerification emailVerification = emailVerificationRepository.findByEmail(emailCodeCheckRequest.getEmail())
                .orElseThrow(RuntimeException::new);

        LocalDateTime expiresAt = emailVerification.getExpiresAt();

        if (LocalDateTime.now().isAfter(expiresAt)) {
            throw new CodeVerificationException("인증 코드가 만료되었습니다.");
        }

        String code = emailVerification.getCode();

        if (!Objects.equals(code, emailCodeCheckRequest.getCode())) {
            throw new CodeVerificationException("인증 코드가 올바르지 않습니다.");
        }

    }
}
