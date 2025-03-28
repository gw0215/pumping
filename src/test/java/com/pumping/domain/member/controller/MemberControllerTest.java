package com.pumping.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.emailverification.fixture.EmailVerificationFixture;
import com.pumping.domain.emailverification.model.EmailVerification;
import com.pumping.domain.emailverification.repository.EmailVerificationRepository;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;
import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.dto.VerifyPasswordRequest;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.service.MemberService;
import com.pumping.global.auth.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    MemberService memberService;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    @Transactional
    void 사용자_저장_API_성공() throws Exception {

        MemberSignUpRequest memberSignUpRequest = MemberFixture.createMemberSignUpRequest();
        String json = objectMapper.writeValueAsString(memberSignUpRequest);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 사용자_삭제_API_성공() throws Exception {

        MemberSignUpRequest memberSignUpRequest = MemberFixture.createMemberSignUpRequest();
        Long id = memberService.save(memberSignUpRequest);
        String token = jwtTokenProvider.createToken(id);

        VerifyPasswordRequest verifyPasswordRequest = MemberFixture.createDeleteMemberRequest();
        String json = objectMapper.writeValueAsString(verifyPasswordRequest);

        mockMvc.perform(delete("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }


}