package com.pumping.domain.exercisehistory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.domain.routine.fixture.RoutineFixture;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryRequest;
import com.pumping.domain.exercisehistory.fixture.RoutineDateFixture;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import com.pumping.domain.exercisehistory.repository.ExerciseHistoryRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class exerciseHistoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoutineRepository routineRepository;

    @Autowired
    ExerciseHistoryRepository exerciseHistoryRepository;

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

        ExerciseHistoryRequest exerciseHistoryRequest = RoutineDateFixture.createRoutineDateRequest(routine.getId());

        String json = objectMapper.writeValueAsString(exerciseHistoryRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(post("/exercise-history")
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

        ExerciseHistory exerciseHistory = RoutineDateFixture.createRoutineDate(member, routine);
        exerciseHistoryRepository.save(exerciseHistory);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(get("/exercise-history")
                        .session(session)
                        .param("performedDate", exerciseHistory.getPerformedDate().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }
}