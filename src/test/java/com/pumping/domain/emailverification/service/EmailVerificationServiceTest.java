package com.pumping.domain.emailverification.service;

import com.pumping.domain.emailverification.exception.CodeVerificationException;
import com.pumping.domain.emailverification.fixture.EmailVerificationFixture;
import com.pumping.domain.emailverification.model.EmailVerification;
import com.pumping.domain.emailverification.repository.EmailVerificationRepository;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailCaptor;

    @Test
    void sendCode_이메일_처음_요청시_메일전송_및_인증정보_DB_저장() {

        String email = "test@example.com";
        when(emailVerificationRepository.findByEmail(email)).thenReturn(Optional.empty());

        emailVerificationService.sendCode(email);

        verify(javaMailSender, atLeastOnce()).send(mailCaptor.capture());
        SimpleMailMessage message = mailCaptor.getValue();
        assertThat(message.getTo()).containsExactly(email);
        assertThat(message.getSubject()).contains("이메일 인증 코드");

        verify(emailVerificationRepository).save(any(EmailVerification.class));
    }

    @Test
    void sendCode_메일_3회_재시도_실패시_DB에_저장되지_않는다() {

        String email = "fail@example.com";
        doThrow(new MailSendException("메일 전송 실패"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        emailVerificationService.sendCode(email);

        verify(javaMailSender, times(3)).send(any(SimpleMailMessage.class));
        verify(emailVerificationRepository, never()).save(any());
    }

    @Test
    void checkCode_올바른_코드일_경우_예외없이_통과한다() {

        String email = "test@example.com";
        String code = "123456";

        EmailVerification verification = new EmailVerification(email, code, LocalDateTime.now().plusMinutes(5));
        when(emailVerificationRepository.findByEmail(email)).thenReturn(Optional.of(verification));

        EmailCodeCheckRequest request = EmailVerificationFixture.createEmailCodeCheckRequest(email, code);

        assertThatCode(() -> emailVerificationService.checkCode(request)).doesNotThrowAnyException();
    }

    @Test
    void checkCode_코드가_불일치할_경우_예외를_던진다() {

        String email = "test@example.com";
        String correctCode = "123456";
        String wrongCode = "999999";

        EmailVerification verification = new EmailVerification(email, correctCode, LocalDateTime.now().plusMinutes(5));
        when(emailVerificationRepository.findByEmail(email)).thenReturn(Optional.of(verification));

        EmailCodeCheckRequest request = new EmailCodeCheckRequest(email, wrongCode);

        assertThatThrownBy(() -> emailVerificationService.checkCode(request))
                .isInstanceOf(CodeVerificationException.class)
                .hasMessage("인증 코드가 올바르지 않습니다.");
    }

    @Test
    void checkCode_코드_만료_예외() {

        String email = "test@example.com";
        String code = "123456";
        LocalDateTime expiredAt = LocalDateTime.now().minusSeconds(5);

        EmailVerification expiredVerification = new EmailVerification(email, code, expiredAt);
        when(emailVerificationRepository.findByEmail(email)).thenReturn(Optional.of(expiredVerification));

        EmailCodeCheckRequest request = new EmailCodeCheckRequest(email, code);

        assertThatThrownBy(() -> emailVerificationService.checkCode(request))
                .isInstanceOf(CodeVerificationException.class)
                .hasMessage("인증 코드가 만료되었습니다.");
    }

    @Test
    void checkCode_인증정보_없으면_예외() {

        String email = "none@example.com";
        when(emailVerificationRepository.findByEmail(email)).thenReturn(Optional.empty());

        EmailCodeCheckRequest request = new EmailCodeCheckRequest(email, "code");

        assertThatThrownBy(() -> emailVerificationService.checkCode(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("이메일을 찾을 수 없습니다. 이메일 : " + email);
    }
}