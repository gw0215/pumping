package com.pumping.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.member.dto.LoginRequest;
import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.dto.VerifyPasswordRequest;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.domain.member.service.MemberService;
import com.pumping.global.common.util.JwtUtil;
import com.pumping.global.config.FirebaseConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    JwtUtil jwtUtil;

    @MockitoBean
    JavaMailSender javaMailSender;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @Test
    @Transactional
    void 회원가입_요청이_정상적으로_처리되면_201_응답() throws Exception {
        MemberSignUpRequest request = MemberFixture.createMemberSignUpRequest();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Transactional
    void 로그인된_사용자가_회원탈퇴하면_200_응답() throws Exception {

        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        String token = jwtUtil.generateToken(member);

        mockMvc.perform(delete("/members")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    void 로그인_정보가_정상이면_200_응답() throws Exception {
        memberService.save(MemberFixture.createMemberSignUpRequest());

        LoginRequest request = MemberFixture.createLoginRequest();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    void 로그인된_사용자가_프로필을_조회하면_200_응답() throws Exception {
        Member member = MemberFixture.createMember();
        memberRepository.save(member);
        String token = jwtUtil.generateToken(member);
        mockMvc.perform(get("/members/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    void 로그인된_사용자가_프로필_이미지를_수정하면_204_응답() throws Exception {
        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        String token = jwtUtil.generateToken(member);

        MockMultipartFile file = new MockMultipartFile(
                "file", "profile.png", MediaType.IMAGE_PNG_VALUE, "changeimage".getBytes()
        );

        mockMvc.perform(multipart(HttpMethod.PATCH, "/members/profile-image")
                        .file(file)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @Transactional
    void 비밀번호가_일치하면_204_응답() throws Exception {
        memberService.save(MemberFixture.createMemberSignUpRequest());
        Member member = memberRepository.findAll().get(0);

        String token = jwtUtil.generateToken(member);

        String json = objectMapper.writeValueAsString(MemberFixture.createVerifyPasswordRequest());

        mockMvc.perform(post("/verify-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @Transactional
    void 이메일이_중복되지_않으면_200_응답() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(get("/verify-email")
                        .param("email", email))
                .andExpect(status().isOk())
                .andDo(print());
    }


}