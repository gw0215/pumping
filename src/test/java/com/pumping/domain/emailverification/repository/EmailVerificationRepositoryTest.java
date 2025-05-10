package com.pumping.domain.emailverification.repository;

import com.pumping.domain.emailverification.fixture.EmailVerificationFixture;
import com.pumping.domain.emailverification.model.EmailVerification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmailVerificationRepositoryTest {

    @Autowired
    EmailVerificationRepository emailVerificationRepository;

    @Test
    void 이메일로_이메일인증_조회_테스트() {
        EmailVerification emailVerification = EmailVerificationFixture.createEmailVerification();
        emailVerificationRepository.save(emailVerification);

        Optional<EmailVerification> result = emailVerificationRepository.findByEmail(emailVerification.getEmail());

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getEmail()).isEqualTo(emailVerification.getEmail());
    }
}