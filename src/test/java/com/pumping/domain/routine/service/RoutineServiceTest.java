package com.pumping.domain.routine.service;

import com.pumping.domain.exercise.fixture.ExerciseFixture;
import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.dto.RoutineDetailResponse;
import com.pumping.domain.routine.dto.RoutineExerciseRequest;
import com.pumping.domain.routine.dto.RoutineExerciseRequests;
import com.pumping.domain.routine.dto.RoutineResponse;
import com.pumping.domain.routine.fixture.RoutineFixture;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routineexercise.dto.ExerciseSetRequest;
import com.pumping.domain.routineexercise.fixture.ExerciseSetFixture;
import com.pumping.domain.routineexercise.fixture.RoutineExerciseFixture;
import com.pumping.domain.routineexercise.model.ExerciseSet;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoutineServiceTest {

    @InjectMocks
    private RoutineService routineService;

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMember();
    }

    @Test
    void 루틴_생성_성공() {

        Exercise exercise = ExerciseFixture.createExerciseWithId(1L);
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        ExerciseSetRequest exerciseSetRequest = ExerciseSetFixture.createExerciseSetRequest();

        List<ExerciseSetRequest> setRequests = List.of(exerciseSetRequest);

        RoutineExerciseRequest routineExerciseRequest =  RoutineExerciseFixture.createRoutineExerciseRequest(exercise.getId(),  setRequests);
        RoutineExerciseRequests routineExerciseRequests = RoutineExerciseFixture.createRoutineExerciseRequests("상체 루틴", List.of(routineExerciseRequest));

        routineService.create(member, routineExerciseRequests);

        verify(exerciseRepository).findById(1L);
        verify(routineRepository).save(any(Routine.class));
    }

    @Test
    void 루틴_생성_실패_운동없음() {
        Long invalidExerciseId = 1L;
        ExerciseSetRequest exerciseSetRequest = ExerciseSetFixture.createExerciseSetRequest();
        List<ExerciseSetRequest> setRequests = List.of(exerciseSetRequest);

        when(exerciseRepository.findById(invalidExerciseId)).thenReturn(Optional.empty());

        RoutineExerciseRequest routineExerciseRequest =
                RoutineExerciseFixture.createRoutineExerciseRequest(invalidExerciseId, setRequests);
        RoutineExerciseRequests routineExerciseRequests =
                RoutineExerciseFixture.createRoutineExerciseRequests("상체 루틴", List.of(routineExerciseRequest));

        assertThatThrownBy(() -> routineService.create(member, routineExerciseRequests))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("운동을 찾을 수 없습니다");
    }

    @Test
    void 루틴_ID로_조회_성공() {

        Exercise exercise = ExerciseFixture.createExerciseWithId(1L);
        Routine routine = RoutineFixture.createRoutine(member, "상체 루틴");

        RoutineExercise routineExercise = RoutineExerciseFixture.createRoutineExercise(routine, exercise);

        ExerciseSet exerciseSet = ExerciseSetFixture.createExerciseSet(routineExercise);

        routineExercise.addExerciseSet(exerciseSet);
        routine.addRoutineExercise(routineExercise);

        when(routineRepository.findById(1L)).thenReturn(Optional.of(routine));

        RoutineDetailResponse result = routineService.findById(1L);

        assertThat(result.name()).isEqualTo("상체 루틴");
        assertThat(result.exercises()).hasSize(1);
        assertThat(result.exercises().get(0).exerciseId()).isEqualTo(1L);
    }

    @Test
    void 루틴_ID로_조회_실패() {
        when(routineRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routineService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("루틴을 찾을 수 없습니다");
    }

    @Test
    void 루틴_전체조회_성공() {
        Routine routine1 =  RoutineFixture.createRoutine(member, "상체 루틴");
        Routine routine2 = RoutineFixture.createRoutine(member, "하체 루틴");
        when(routineRepository.findAllByMemberId(member.getId())).thenReturn(List.of(routine1, routine2));

        List<RoutineResponse> result = routineService.findAll(member.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting("routineName").containsExactly("상체 루틴", "하체 루틴");
    }
}