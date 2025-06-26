package com.pumping.domain.exercisehistory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.config.MyContextInitializer;
import com.pumping.domain.exercise.fixture.ExerciseFixture;
import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.model.ExercisePart;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryUpdateRequest;
import com.pumping.domain.exercisehistory.model.ExerciseHistoryStatus;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.domain.performedexercise.dto.PerformedExerciseRequest;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetRequest;
import com.pumping.domain.performedexercise.model.PerformedExercise;
import com.pumping.domain.performedexercise.model.PerformedExerciseSet;
import com.pumping.domain.performedexercise.repository.PerformedExerciseRepository;
import com.pumping.domain.performedexercise.repository.PerformedExerciseSetRepository;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@ContextConfiguration(initializers = MyContextInitializer.class)
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

    @Autowired
    PerformedExerciseSetRepository performedExerciseSetRepository;

    @Autowired
    PerformedExerciseRepository performedExerciseRepository;

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

        Routine routine = RoutineFixture.createRoutine(member,"루틴이름");
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

        Exercise exercise1 = ExerciseFixture.createExercise();
        exerciseRepository.save(exercise1);

        Exercise exercise2 = ExerciseFixture.createExercise();
        exerciseRepository.save(exercise2);

        Routine routine = RoutineFixture.createRoutine(member,"루틴이름");
        routineRepository.save(routine);

        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine);
        PerformedExercise performedExercise1 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory, exercise1);
        PerformedExercise performedExercise2 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory, exercise2);
        PerformedExerciseSet performedExerciseSet1 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise1);
        PerformedExerciseSet performedExerciseSet2 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise1);
        PerformedExerciseSet performedExerciseSet3 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise2);

        performedExercise1.addPerformedExerciseSet(performedExerciseSet1);
        performedExercise2.addPerformedExerciseSet(performedExerciseSet2);
        performedExercise2.addPerformedExerciseSet(performedExerciseSet3);

        exerciseHistory.addPerformedExercise(performedExercise1);
        exerciseHistory.addPerformedExercise(performedExercise2);
        exerciseHistoryRepository.save(exerciseHistory);

        List<PerformedExerciseSetRequest> addSets = ExerciseHistoryFixture.createPerformedExerciseSetRequests(3, 0L, performedExercise1.getId());
        List<PerformedExerciseSetRequest> updateSets = ExerciseHistoryFixture.createPerformedExerciseSetRequests(3, performedExerciseSet1.getId(), performedExercise1.getId());
        List<Long> deletedSetIds = List.of(performedExerciseSet2.getId());
        List<PerformedExerciseSetRequest> performedExerciseSetRequests = ExerciseHistoryFixture.createPerformedExerciseSetRequests(3, performedExerciseSet3.getId(), performedExercise2.getId());
        List<PerformedExerciseRequest> performedExerciseRequests = ExerciseHistoryFixture.createPerformedExerciseRequests(4, exercise2.getId(), performedExerciseSetRequests);

        ExerciseHistoryUpdateRequest exerciseHistoryUpdateRequest = ExerciseHistoryFixture.createExerciseHistoryUpdateRequest(addSets, updateSets, deletedSetIds, performedExerciseRequests);

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
    void 운동_기록_날짜_조회_API_성공() throws Exception {

        Routine routine = RoutineFixture.createRoutine(member,"루틴이름");
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

        Routine routine = RoutineFixture.createRoutine(member,"루틴이름");
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

        Routine routine = RoutineFixture.createRoutine(member,"루틴이름");
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

        Routine routine = RoutineFixture.createRoutine(member,"루틴이름");
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

    @Test
    @Transactional
    void 세트_체크_API_성공() throws Exception {
        Routine routine = routineRepository.save(RoutineFixture.createRoutine(member,"루틴이름"));
        Exercise exercise = exerciseRepository.save(ExerciseFixture.createExercise());

        ExerciseHistory exerciseHistory = exerciseHistoryRepository.save(ExerciseHistoryFixture.createExerciseHistory(member, routine));
        PerformedExercise performedExercise = performedExerciseRepository.save(ExerciseHistoryFixture.createPerformedExercise(exerciseHistory, exercise));
        PerformedExerciseSet performedExerciseSet = performedExerciseSetRepository.save(ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise));

        mockMvc.perform(patch("/performed-exercise-set/{performedExerciseSetId}", performedExerciseSet.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 주간_운동_부위별_세트수_조회_API_성공() throws Exception {
        Routine routine = routineRepository.save(RoutineFixture.createRoutine(member,"루틴이름"));
        Exercise exercise = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.CHEST));

        LocalDate performedDate = LocalDate.now().minusDays(2);
        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine, performedDate, ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory, exercise);
        PerformedExerciseSet performedExerciseSet = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise, 2f,2, true);
        performedExercise.addPerformedExerciseSet(performedExerciseSet);
        exerciseHistory.addPerformedExercise(performedExercise);
        exerciseHistoryRepository.save(exerciseHistory);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(get("/exercise-history/exercise-part-analyze")
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 이번달과_지난달_운동량_비교_API_성공() throws Exception {
        Routine routine = routineRepository.save(RoutineFixture.createRoutine(member,"루틴이름"));
        Exercise exercise = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.BACK));

        LocalDate lastMonthDate = LocalDate.now().minusMonths(1).withDayOfMonth(10);
        ExerciseHistory lastMonthHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine, lastMonthDate, ExerciseHistoryStatus.COMPLETED);
        PerformedExercise lastMonthExercise = ExerciseHistoryFixture.createPerformedExercise(lastMonthHistory, exercise);
        PerformedExerciseSet performedExerciseSet1 = ExerciseHistoryFixture.createPerformedExerciseSet(lastMonthExercise, 3f,3,true);
        lastMonthHistory.addPerformedExercise(lastMonthExercise);
        lastMonthExercise.addPerformedExerciseSet(performedExerciseSet1);
        exerciseHistoryRepository.save(lastMonthHistory);

        LocalDate thisMonthDate = LocalDate.now().withDayOfMonth(10);
        ExerciseHistory thisMonthHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine, thisMonthDate, ExerciseHistoryStatus.COMPLETED);
        PerformedExercise thisMonthExercise = ExerciseHistoryFixture.createPerformedExercise(thisMonthHistory, exercise);
        PerformedExerciseSet performedExerciseSet2 = ExerciseHistoryFixture.createPerformedExerciseSet(thisMonthExercise, 4f,4,true);
        thisMonthHistory.addPerformedExercise(thisMonthExercise);
        thisMonthExercise.addPerformedExerciseSet(performedExerciseSet2);
        exerciseHistoryRepository.save(thisMonthHistory);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(get("/exercise-history/last-month-compare")
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 운동_세트_체크_API_성공() throws Exception {
        Routine routine = RoutineFixture.createRoutine(member,"루틴이름");
        routineRepository.save(routine);

        Exercise exercise = ExerciseFixture.createExercise();
        exerciseRepository.save(exercise);

        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine);
        PerformedExercise performedExercise = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory, exercise);
        PerformedExerciseSet performedExerciseSet = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise);

        exerciseHistory.addPerformedExercise(performedExercise);
        performedExercise.addPerformedExerciseSet(performedExerciseSet);

        exerciseHistoryRepository.save(exerciseHistory);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(patch("/performed-exercise-set/{performedExerciseSetId}", performedExerciseSet.getId())
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void 부위별_상위_5_운동_조회_API_성공() throws Exception {
        Routine routine = routineRepository.save(RoutineFixture.createRoutine(member,"루틴이름"));
        Exercise exercise1 = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.CHEST));
        Exercise exercise2 = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.BACK));
        Exercise exercise3 = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.LEGS));

        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        ExerciseHistory history1 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().minusDays(3), ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise1 = ExerciseHistoryFixture.createPerformedExercise(history1, exercise1);
        PerformedExerciseSet performedSet1 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise1, 3f, 10, true);
        performedExercise1.addPerformedExerciseSet(performedSet1);
        history1.addPerformedExercise(performedExercise1);
        exerciseHistoryRepository.save(history1);

        ExerciseHistory history2 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().minusDays(5), ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise2 = ExerciseHistoryFixture.createPerformedExercise(history2, exercise2);
        PerformedExerciseSet performedSet2 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise2, 4f, 8, true);
        performedExercise2.addPerformedExerciseSet(performedSet2);
        history2.addPerformedExercise(performedExercise2);
        exerciseHistoryRepository.save(history2);

        ExerciseHistory history3 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().minusDays(1), ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise3 = ExerciseHistoryFixture.createPerformedExercise(history3, exercise3);
        PerformedExerciseSet performedSet3 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise3, 2f, 12, true);
        performedExercise3.addPerformedExerciseSet(performedSet3);
        history3.addPerformedExercise(performedExercise3);
        exerciseHistoryRepository.save(history3);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(get("/exercise-history/top5")
                        .session(session)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

}