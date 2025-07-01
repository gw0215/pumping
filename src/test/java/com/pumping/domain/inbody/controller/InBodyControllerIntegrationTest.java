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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class InBodyControllerIntegrationTest {

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
    void saveInBody_유효한_요청일_경우_201_응답을_반환한다() throws Exception {
        InBodyRequest inBodyRequest = InBodyFixture.createInBodyRequest();
        String json = objectMapper.writeValueAsString(inBodyRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(post("/inbody")
                        .session(session)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Transactional
    void saveInBody_필수_입력값이_누락된_경우_400_에러를_반환한다() throws Exception {

        InBodyRequest invalidRequest = new InBodyRequest(null, 20.0f, 15.0f, LocalDate.now());
        String json = objectMapper.writeValueAsString(invalidRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(post("/inbody")
                        .session(session)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());
    }

    @Test
    @Transactional
    void findRecentInBody_인바디가_존재할_경우_200_응답과_데이터를_반환한다() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        InBody inbody = InBodyFixture.createInbody(member);
        inBodyRepository.save(inbody);

        mockMvc.perform(get("/inbody/recent")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(inbody.getWeight()))
                .andExpect(jsonPath("$.date").value(inbody.getDate().toString()))
                .andDo(print());
    }

    @Test
    @Transactional
    void findRecentInBody_인바디가_존재하지_않을_경우_204_응답을_반환한다() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(get("/inbody/recent")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @Transactional
    void findInBodyByDate_지정된_날짜_범위에_해당하는_데이터가_있으면_200_응답을_반환한다() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        InBody inBody = InBodyFixture.createInbody(member);
        inBodyRepository.save(inBody);

        mockMvc.perform(get("/inbody")
                        .session(session)
                        .param("from", LocalDate.now().minusDays(3L).toString())
                        .param("to", LocalDate.now().plusDays(3L).toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].weight").value(inBody.getWeight()))
                .andDo(print());
    }


}