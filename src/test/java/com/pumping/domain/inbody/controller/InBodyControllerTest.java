package com.pumping.domain.inbody.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.inbody.dto.InBodyRequest;
import com.pumping.domain.inbody.fixture.InBodyFixture;
import com.pumping.domain.inbody.model.InBody;
import com.pumping.domain.inbody.repository.InBodyRepository;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class InBodyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    InBodyRepository inBodyRepository;

    Member member;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMember();
        memberRepository.save(member);
    }

    @Test
    @Transactional
    void 인바디_저장_API_성공() throws Exception {

        InBodyRequest inBodyRequest = InBodyFixture.createInBodyRequest();

        String json = objectMapper.writeValueAsString(inBodyRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(post("/inbody")
                        .session(session)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    void 최근_인바디_조회_API_성공() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        InBody inbody = InBodyFixture.createInbody(member);
        inBodyRepository.save(inbody);

        mockMvc.perform(get("/inbody/recent")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    void 인바디_날짜_조회_API_성공() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(get("/inbody")
                        .session(session)
                        .param("from", LocalDate.now().minusDays(3L).toString())
                        .param("to", LocalDate.now().plusDays(3L).toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }


}