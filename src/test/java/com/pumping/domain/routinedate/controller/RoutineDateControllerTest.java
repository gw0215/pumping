package com.pumping.domain.routinedate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.domain.routine.fixture.RoutineFixture;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routinedate.dto.RoutineDateRequest;
import com.pumping.domain.routinedate.fixture.RoutineDateFixture;
import com.pumping.domain.routinedate.model.RoutineDate;
import com.pumping.domain.routinedate.repository.RoutineDateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
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
class RoutineDateControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoutineRepository routineRepository;

    @Autowired
    RoutineDateRepository routineDateRepository;

    @MockitoBean
    JavaMailSender javaMailSender;

    Member member;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMember();
        memberRepository.save(member);
    }


    @Test
    @Transactional
    void 루틴_날짜_저장_API_성공() throws Exception {

        Routine routine = RoutineFixture.createRoutine(member);
        routineRepository.save(routine);

        RoutineDateRequest routineDateRequest = RoutineDateFixture.createRoutineDateRequest(routine.getId());

        String json = objectMapper.writeValueAsString(routineDateRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(post("/routine-date")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    void 루틴_날짜_조회_API_성공() throws Exception {

        Routine routine = RoutineFixture.createRoutine(member);
        routineRepository.save(routine);

        RoutineDate routineDate = RoutineDateFixture.createRoutineDate(routine);
        routineDateRepository.save(routineDate);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(get("/routine-date")
                        .session(session)
                        .param("routineDate", routineDate.getPerformedDate().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }
}