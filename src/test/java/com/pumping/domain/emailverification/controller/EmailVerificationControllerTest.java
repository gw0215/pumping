package com.pumping.domain.emailverification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.emailverification.fixture.EmailVerificationFixture;
import com.pumping.domain.emailverification.model.EmailVerification;
import com.pumping.domain.emailverification.repository.EmailVerificationRepository;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;
import com.pumping.domain.member.repository.MemberRepository;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmailVerificationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    EmailVerificationRepository emailVerificationRepository;

    @Test
    @Transactional
    void 이메일_전송_API_성공() throws Exception {

        String email = "email@fit.com";

        mockMvc.perform(get("/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", email))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 이메일_검증_API_성공() throws Exception {

        EmailVerification emailVerification = EmailVerificationFixture.createEmailVerification();
        emailVerificationRepository.save(emailVerification);

        EmailCodeCheckRequest emailCodeCheckRequest = EmailVerificationFixture.createEmailCodeCheckRequest();
        String json = objectMapper.writeValueAsString(emailCodeCheckRequest);

        mockMvc.perform(post("/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }
}