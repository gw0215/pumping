package com.pumping.domain.emailverification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.emailverification.fixture.EmailVerificationFixture;
import com.pumping.domain.emailverification.model.EmailVerification;
import com.pumping.domain.emailverification.repository.EmailVerificationRepository;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmailVerificationControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EmailVerificationRepository emailVerificationRepository;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    @Transactional
    void 이메일_전송_API_성공() throws Exception {
        String email = "test@pumping.com";

        mockMvc.perform(get("/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", email))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Transactional
    void 이메일_검증_API_성공() throws Exception {
        EmailVerification emailVerification = EmailVerificationFixture.createEmailVerification(
                "test@pumping.com", "12345", LocalDateTime.now().plusMinutes(3)
        );
        emailVerificationRepository.save(emailVerification);

        EmailCodeCheckRequest request = new EmailCodeCheckRequest("test@pumping.com", "12345");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @Transactional
    void 이메일_코드_검증_실패_만료된_코드() throws Exception {
        String email = "expired@pumping.com";
        String code = "12345";
        LocalDateTime expiredAt = LocalDateTime.now().minusMinutes(1);

        EmailVerification expiredVerification = EmailVerificationFixture.createEmailVerification(email, code, expiredAt);
        emailVerificationRepository.save(expiredVerification);

        EmailCodeCheckRequest request = new EmailCodeCheckRequest(email, code);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("인증 코드가 만료되었습니다."))
                .andDo(print());
    }

    @Test
    @Transactional
    void 이메일_코드_검증_실패_코드_불일치() throws Exception {
        EmailVerification saved = EmailVerificationFixture.createEmailVerification(
                "wrongcode@pumping.com", "999999", LocalDateTime.now().plusMinutes(3)
        );
        emailVerificationRepository.save(saved);

        EmailCodeCheckRequest request = new EmailCodeCheckRequest("wrongcode@pumping.com", "12345");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("인증 코드가 올바르지 않습니다."))
                .andDo(print());
    }

    @Test
    @Transactional
    void 이메일_코드_검증_실패_존재하지_않는_이메일() throws Exception {
        EmailCodeCheckRequest request = new EmailCodeCheckRequest("notfound@pumping.com", "12345");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("이메일을 찾을 수 없습니다. 이메일 : " + request.getEmail()))
                .andDo(print());
    }

    @Test
    @Transactional
    void 이메일_코드_검증_실패_형식오류() throws Exception {
        EmailCodeCheckRequest request = new EmailCodeCheckRequest("invalid-email", "12345");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 이메일 형식이어야 합니다."))
                .andDo(print());
    }
}