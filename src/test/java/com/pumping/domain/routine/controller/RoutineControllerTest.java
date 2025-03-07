package com.pumping.domain.routine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.exercise.fixture.ExerciseFixture;
import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.domain.routine.dto.RoutineExerciseRequest;
import com.pumping.domain.routine.dto.RoutineExerciseRequests;
import com.pumping.domain.routine.fixture.RoutineFixture;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routineexercise.fixture.RoutineExerciseFixture;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import com.pumping.domain.routineexercise.repository.RoutineExerciseRepository;
import com.pumping.global.auth.jwt.JwtTokenProvider;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RoutineControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoutineRepository routineRepository;

    @Autowired
    RoutineExerciseRepository routineExerciseRepository;

    @MockitoBean
    JavaMailSender javaMailSender;

    Member member;

    String token;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMember();
        memberRepository.save(member);

        token = jwtTokenProvider.createToken(member.getId());
    }


    @Test
    @Transactional
    void 루틴_저장_API_성공() throws Exception {

        Exercise exercise = ExerciseFixture.createExercise();
        exerciseRepository.save(exercise);

        List<RoutineExerciseRequest> routineExerciseRequestList = RoutineExerciseFixture.createRoutineExerciseRequests(5);
        RoutineExerciseRequests routineExerciseRequests = RoutineExerciseFixture.createRoutineExerciseRequests(routineExerciseRequestList);

        String json = objectMapper.writeValueAsString(routineExerciseRequests);

        mockMvc.perform(post("/routines")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    void 사용자의_모든_루틴_조회_API_성공() throws Exception {

        Exercise exercise = ExerciseFixture.createExercise();
        Routine routine = RoutineFixture.createRoutine(member);

        RoutineExercise routineExercise = RoutineExerciseFixture.createRoutineExercise(routine, exercise);

        exercise.getRoutineExercise().add(routineExercise);
        routine.getRoutineExercises().add(routineExercise);
        routineRepository.save(routine);
        exerciseRepository.save(exercise);

        mockMvc.perform(get("/routines")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

}