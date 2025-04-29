package com.pumping.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.member.dto.LoginRequest;
import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.dto.VerifyPasswordRequest;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.domain.member.service.MemberService;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    MemberRepository memberRepository;

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


        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(delete("/members")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 로그인_API_성공() throws Exception {
        MemberSignUpRequest memberSignUpRequest = MemberFixture.createMemberSignUpRequest();
        Long id = memberService.save(memberSignUpRequest);

        LoginRequest loginRequest = MemberFixture.createLoginRequest();
        String json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 로그아웃_API_성공() throws Exception {
        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(post("/logout")
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 프로필_조회_API_성공() throws Exception {
        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(get("/members/profile")
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 프로필_이미지_수정_API_성공() throws Exception {
        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "changeimage".getBytes()
        );

        mockMvc.perform(multipart(HttpMethod.PATCH,"/members/profile-image")
                        .file(file)
                        .session(session)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 비밀번호_검증_API_성공() throws Exception {
        MemberSignUpRequest memberSignUpRequest = MemberFixture.createMemberSignUpRequest();
        Long id = memberService.save(memberSignUpRequest);

        Optional<Member> optionalMember = memberRepository.findById(id);
        Assertions.assertThat(optionalMember).isPresent();
        Member member = optionalMember.get();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        VerifyPasswordRequest verifyPasswordRequest = MemberFixture.createVerifyPasswordRequest();
        String json = objectMapper.writeValueAsString(verifyPasswordRequest);

        mockMvc.perform(post("/verify-password")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 이메일_중복검사_API_성공() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(get("/verify-email")
                        .param("email", email))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }


}