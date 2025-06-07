package com.pumping.domain.emailverification.service;

import com.pumping.domain.emailverification.exception.CodeVerificationException;
import com.pumping.domain.emailverification.model.EmailVerification;
import com.pumping.domain.emailverification.repository.EmailVerificationRepository;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;
import com.pumping.global.common.util.RandomCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final String MAIL_SUBJECT = "[PUMPING] 이메일 인증 코드";
    private static final Duration CODE_DURATION = Duration.ofMinutes(5);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender javaMailSender;

    @Async("emailExecutor")
    @Transactional
    public void sendCode(String email) {
        String code = RandomCodeGenerator.generateCode();

        boolean emailSent = false;
        int attempt = 0;

        while (attempt < MAX_RETRIES && !emailSent) {
            try {
                sendEmail(email, code);
                emailSent = true;
                log.info("이메일 전송 성공: {} (시도 {}회)", email, attempt + 1);
            } catch (Exception e) {
                attempt++;
                log.warn("이메일 전송 실패 (시도 {}회): {}", attempt, email, e);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.error("이메일 전송 재시도 중 인터럽트 발생", ex);
                    break;
                }
            }
        }

        if (!emailSent) {
            log.error("이메일 전송 최종 실패: {}", email);
            return;
        }

        saveOrUpdateVerification(email, code);
    }

    private void sendEmail(String email, String code) {
        String mailText = String.format("인증 코드: %s\n앱으로 돌아가서 인증을 완료해주세요", code);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(MAIL_SUBJECT);
        message.setText(mailText);
        javaMailSender.send(message);
    }

    private void saveOrUpdateVerification(String email, String code) {
        LocalDateTime expireTime = LocalDateTime.now().plus(CODE_DURATION);
        Optional<EmailVerification> optional = emailVerificationRepository.findByEmail(email);
        if (optional.isPresent()) {
            EmailVerification verification = optional.get();
            verification.updateCode(code);
            verification.updateExpiredAt(expireTime);
        } else {
            emailVerificationRepository.save(new EmailVerification(email, code, expireTime));
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
