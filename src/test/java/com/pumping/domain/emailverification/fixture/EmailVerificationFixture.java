package com.pumping.domain.emailverification.fixture;

import com.pumping.domain.emailverification.model.EmailVerification;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

public abstract class EmailVerificationFixture {

    private static final String EMAIL = "email@fit.com";
    private static final String CODE = "code12";
    private static final LocalDateTime EXPIRES_AT = LocalDateTime.now().plus(Duration.ofMinutes(5));

    public static EmailVerification createEmailVerification() {
        return new EmailVerification(EMAIL, CODE, EXPIRES_AT);
    }

    public static EmailCodeCheckRequest createEmailCodeCheckRequest() {
        return new EmailCodeCheckRequest(EMAIL, CODE);
    }


}
