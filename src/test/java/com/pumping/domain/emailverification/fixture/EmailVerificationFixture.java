package com.pumping.domain.emailverification.fixture;

import com.pumping.domain.emailverification.model.EmailVerification;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class EmailVerificationFixture {

    private static final String EMAIL = "email@fit.com";
    private static final String CODE = "code12";
    private static final LocalDateTime EXPIRES_AT = LocalDateTime.now().plus(Duration.ofMinutes(5));

    public static EmailVerification createEmailVerification() {
        return new EmailVerification(EMAIL, CODE, EXPIRES_AT);
    }

    public static EmailVerification createEmailVerification(String email, String code, LocalDateTime expiredAt) {
        return new EmailVerification(email, code, expiredAt);
    }

    public static EmailCodeCheckRequest createEmailCodeCheckRequest() {
        return new EmailCodeCheckRequest(EMAIL, CODE);
    }

    public static EmailCodeCheckRequest createEmailCodeCheckRequest(String email, String code) {
        return new EmailCodeCheckRequest(email, code);
    }


}
