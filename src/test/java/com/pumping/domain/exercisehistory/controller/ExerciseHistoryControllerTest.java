package com.pumping.domain.exercisehistory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.exercise.fixture.ExerciseFixture;
import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryUpdateRequest;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.domain.performedexerciseset.dto.PerformedExerciseSetRequest;
import com.pumping.domain.routine.fixture.RoutineFixture;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryRequest;
import com.pumping.domain.exercisehistory.fixture.ExerciseHistoryFixture;
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

import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExerciseHistoryControllerTest {

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

    @Autowired
    ExerciseRepository exerciseRepository;

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
    void 운동_기록_저장_API_성공() throws Exception {

        Routine routine = RoutineFixture.createRoutine(member);
        routineRepository.save(routine);

        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine);
        exerciseHistoryRepository.save(exerciseHistory);

        ExerciseHistoryRequest exerciseHistoryRequest = ExerciseHistoryFixture.createRoutineDateRequest(routine.getId());

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
    void 운동_기록_수정_API_성공() throws Exception {

        Exercise exercise = ExerciseFixture.createExercise();
        exerciseRepository.save(exercise);

        Routine routine = RoutineFixture.createRoutine(member);
        routineRepository.save(routine);

        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine);
        exerciseHistoryRepository.save(exerciseHistory);

        List<PerformedExerciseSetRequest> performedExerciseSetRequests = ExerciseHistoryFixture.createPerformedExerciseSetRequests(3, exercise.getId());
        ExerciseHistoryUpdateRequest exerciseHistoryUpdateRequest = ExerciseHistoryFixture.createExerciseHistoryUpdateRequest(performedExerciseSetRequests);

        String json = objectMapper.writeValueAsString(exerciseHistoryUpdateRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(patch("/exercise-history/{exerciseHistoryId}",exerciseHistory.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    void 운동_기록_아이디_조회_API_성공() throws Exception {

        Routine routine = RoutineFixture.createRoutine(member);
        routineRepository.save(routine);

        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine);
        exerciseHistoryRepository.save(exerciseHistory);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(get("/exercise-history/{exerciseHistoryId}",exerciseHistory.getId())
                        .session(session)
                        .param("performedDate", exerciseHistory.getPerformedDate().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    void 운동_기록_날짜_조회_API_성공() throws Exception {

        Routine routine = RoutineFixture.createRoutine(member);
        routineRepository.save(routine);

        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine);
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

    @Test
    @Transactional
    void 운동_기록_아이디_삭제_API_성공() throws Exception {

        Routine routine = RoutineFixture.createRoutine(member);
        routineRepository.save(routine);

        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine);
        exerciseHistoryRepository.save(exerciseHistory);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(delete("/exercise-history/{exerciseHistoryId}",exerciseHistory.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    void 운동_기록_주간_기록_API_성공() throws Exception {

        Routine routine = RoutineFixture.createRoutine(member);
        routineRepository.save(routine);

        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine);
        exerciseHistoryRepository.save(exerciseHistory);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(get("/exercise-history/weekstatus",exerciseHistory.getId())
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    void 운동_상태_완료_API_성공() throws Exception {

        Routine routine = RoutineFixture.createRoutine(member);
        routineRepository.save(routine);

        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine);
        exerciseHistoryRepository.save(exerciseHistory);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(patch("/exercise-history/{exerciseHistoryId}/end",exerciseHistory.getId())
                        .param("endTime", LocalTime.now().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }
}