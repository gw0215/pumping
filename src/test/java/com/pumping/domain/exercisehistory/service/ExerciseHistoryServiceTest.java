package com.pumping.domain.exercisehistory.service;

import com.pumping.domain.exercise.fixture.ExerciseFixture;
import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.model.ExercisePart;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.exercisehistory.dto.*;
import com.pumping.domain.exercisehistory.fixture.ExerciseHistoryFixture;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import com.pumping.domain.exercisehistory.model.ExerciseHistoryStatus;
import com.pumping.domain.exercisehistory.repository.ExerciseHistoryRepository;
import com.pumping.domain.exercisehistory.repository.MonthlyPartVolumeDto;
import com.pumping.domain.exercisehistory.repository.TopExerciseDto;
import com.pumping.domain.exercisehistory.repository.WeeklyExerciseHistoryStatsDto;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.performedexercise.dto.PerformedExerciseRequest;
import com.pumping.domain.performedexercise.dto.PerformedExerciseResponse;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetRequest;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetResponse;
import com.pumping.domain.performedexercise.model.PerformedExercise;
import com.pumping.domain.performedexercise.model.PerformedExerciseSet;
import com.pumping.domain.performedexercise.repository.PerformedExerciseSetRepository;
import com.pumping.domain.routine.fixture.RoutineFixture;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routineexercise.fixture.ExerciseSetFixture;
import com.pumping.domain.routineexercise.fixture.RoutineExerciseFixture;
import com.pumping.domain.routineexercise.model.ExerciseSet;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseHistoryServiceTest {

    @InjectMocks
    private ExerciseHistoryService exerciseHistoryService;

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private ExerciseHistoryRepository exerciseHistoryRepository;

    @Mock
    private PerformedExerciseSetRepository performedExerciseSetRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Test
    void 루틴으로부터_운동수행이력이_생성된다() {

        Member member = MemberFixture.createMember();

        Exercise exercise = ExerciseFixture.createExercise();
        Routine routine = RoutineFixture.createRoutine(member, "루틴이름");
        RoutineExercise routineExercise = RoutineExerciseFixture.createRoutineExercise(routine, exercise);
        ExerciseSet set1 = ExerciseSetFixture.createExerciseSet(routineExercise);
        ExerciseSet set2 = ExerciseSetFixture.createExerciseSet(routineExercise);

        routineExercise.addExerciseSet(set1);
        routineExercise.addExerciseSet(set2);

        routine.addRoutineExercise(routineExercise);

        LocalDate date = LocalDate.of(2025, 6, 22);

        when(routineRepository.findById(routine.getId())).thenReturn(Optional.of(routine));

        exerciseHistoryService.save(member, routine.getId(), date);

        ArgumentCaptor<ExerciseHistory> captor = ArgumentCaptor.forClass(ExerciseHistory.class);
        verify(exerciseHistoryRepository).save(captor.capture());

        ExerciseHistory saved = captor.getValue();
        assertThat(saved.getMember()).isEqualTo(member);
        assertThat(saved.getRoutine()).isEqualTo(routine);
        assertThat(saved.getPerformedDate()).isEqualTo(date);
        assertThat(saved.getPerformedExercises()).hasSize(1);

        PerformedExercise performed = saved.getPerformedExercises().get(0);
        assertThat(performed.getExercise()).isEqualTo(exercise);
        assertThat(performed.getPerformedExerciseSets()).hasSize(2);

        PerformedExerciseSet performedSet1 = performed.getPerformedExerciseSets().get(0);
        assertThat(performedSet1.getWeight()).isEqualTo(set1.getWeight());
        assertThat(performedSet1.getRepetition()).isEqualTo(set1.getRepetition());
    }

    @Test
    void 존재하지_않는_루틴이면_예외_발생() {

        when(routineRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                exerciseHistoryService.save(new Member(), 999L, LocalDate.now())
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void 수행기록을_성공적으로_업데이트한다() {

        Member member = MemberFixture.createMember();
        Routine routine = RoutineFixture.createRoutine(member, "루틴");
        Exercise exercise = ExerciseFixture.createExercise();

        ExerciseHistory history = ExerciseHistoryFixture.createExerciseHistory(member, routine);
        PerformedExercise performedExercise = ExerciseHistoryFixture.createPerformedExerciseWithId(history, exercise,10L);

        PerformedExerciseSet set1 = ExerciseHistoryFixture.createPerformedExerciseSetWithId(performedExercise,100L);
        PerformedExerciseSet set2 = ExerciseHistoryFixture.createPerformedExerciseSetWithId(performedExercise,200L);

        performedExercise.addPerformedExerciseSet(set1);
        performedExercise.addPerformedExerciseSet(set2);
        history.addPerformedExercise(performedExercise);

        when(exerciseHistoryRepository.findById(1L)).thenReturn(Optional.of(history));

        Exercise newExercise = ExerciseFixture.createExerciseWithId(2L);
        when(exerciseRepository.findById(2L)).thenReturn(Optional.of(newExercise));

        List<Long> deletedSetIds = List.of(200L);
        List<PerformedExerciseSetRequest> updatedSets =
                List.of(ExerciseHistoryFixture.createPerformedExerciseSetRequest(100L, 10L));

        PerformedExerciseSetRequest addedSet =
                new PerformedExerciseSetRequest(null, 10L, 80f, 6, 2, true);
        List<PerformedExerciseSetRequest> addedSets = List.of(addedSet);

        List<PerformedExerciseSetRequest> newExerciseSets =
                List.of(new PerformedExerciseSetRequest(null, null, 90f, 5, 3, false));
        List<PerformedExerciseRequest> newExercises =
                List.of(ExerciseHistoryFixture.createPerformedExerciseRequest(2L, newExerciseSets));

        ExerciseHistoryUpdateRequest request =
                ExerciseHistoryFixture.createExerciseHistoryUpdateRequest(addedSets, updatedSets, deletedSetIds, newExercises);

        exerciseHistoryService.update(1L, request);
        assertThat(history.getPerformedExercises()).hasSize(2);

        PerformedExercise updated = history.getPerformedExercises().get(0);
        assertThat(updated.getPerformedExerciseSets()).hasSize(2);

        PerformedExerciseSet updatedSet = updated.getPerformedExerciseSets().get(0);
        assertThat(updatedSet.getWeight()).isEqualTo(ExerciseHistoryFixture.WEIGHT);
        assertThat(updatedSet.getRepetition()).isEqualTo(ExerciseHistoryFixture.REPETITION);
        assertThat(updatedSet.getSetCount()).isEqualTo(ExerciseHistoryFixture.SET_COUNT);
        assertThat(updatedSet.getCompleted()).isEqualTo(false);

    }

    @Test
    void 주어진_회원의_일주일간_운동_통계를_정상적으로_계산한다() {

        Member member = MemberFixture.createMemberWithId(1L);

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        WeeklyExerciseHistoryStatsDto day1 = mock(WeeklyExerciseHistoryStatsDto.class);
        when(day1.getPerformedDate()).thenReturn(monday);
        when(day1.getTotalSeconds()).thenReturn(600L);
        when(day1.getTotalVolume()).thenReturn(100.0f);

        WeeklyExerciseHistoryStatsDto day2 = mock(WeeklyExerciseHistoryStatsDto.class);
        when(day2.getPerformedDate()).thenReturn(monday.plusDays(1));
        when(day2.getTotalSeconds()).thenReturn(300L);
        when(day2.getTotalVolume()).thenReturn(50.0f);

        WeeklyExerciseHistoryStatsDto day3 = mock(WeeklyExerciseHistoryStatsDto.class);
        when(day3.getPerformedDate()).thenReturn(monday.plusDays(2));
        when(day3.getTotalSeconds()).thenReturn(null);
        when(day3.getTotalVolume()).thenReturn(null);

        List<WeeklyExerciseHistoryStatsDto> stats = List.of(day1, day2, day3);

        when(exerciseHistoryRepository.findWeeklyStats(member.getId(), monday, monday.plusDays(6)))
                .thenReturn(stats);

        ExerciseHistoryWeekStatusResponse response = exerciseHistoryService.weekStatus(member);

        assertThat(response.totalSeconds()).isEqualTo(900L);
        assertThat(response.totalVolume()).isEqualTo(150.0f);

        assertThat(response.totalSecondsPerDay()).containsExactly(
                600L, 300L, 0L, 0L, 0L, 0L, 0L
        );

        assertThat(response.totalVolumePerDay()).containsExactly(
                100.0f, 50.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f
        );
    }

    @Test
    void 루틴수행기록이_정상적으로_완료된다() {

        Long historyId = 1L;
        LocalTime performedTime = LocalTime.of(1, 30);

        ExerciseHistory exerciseHistory = mock(ExerciseHistory.class);

        when(exerciseHistoryRepository.findById(historyId))
                .thenReturn(Optional.of(exerciseHistory));

        exerciseHistoryService.endExerciseHistory(historyId, performedTime);

        verify(exerciseHistory).updatePerformedTime(performedTime);
        verify(exerciseHistory).updatePerformedRoutineStatus(ExerciseHistoryStatus.COMPLETED);
    }

    @Test
    void 루틴수행기록이_존재하지_않으면_예외를_던진다() {

        Long invalidId = 99L;
        LocalTime performedTime = LocalTime.of(0, 45);

        when(exerciseHistoryRepository.findById(invalidId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseHistoryService.endExerciseHistory(invalidId, performedTime))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("루틴 수행 기록을 찾을 수 없습니다");
    }

    @Test
    void 존재하지_않는_운동기록이면_empty를_반환한다() {
        Long memberId = 1L;
        LocalDate date = LocalDate.of(2024, 6, 22);

        when(exerciseHistoryRepository.findByMemberIdAndPerformedDate(memberId, date))
                .thenReturn(Optional.empty());

        Optional<ExerciseHistoryResponse> result = exerciseHistoryService.findByMemberIdAndPerformedDate(memberId, date);

        assertThat(result).isEmpty();
    }

    @Test
    void 운동기록이_존재하면_정상적으로_응답을_생성한다() {

        Member member = MemberFixture.createMemberWithId(1L);
        LocalDate date = LocalDate.of(2024, 6, 22);

        Exercise exercise = ExerciseFixture.createExerciseWithId(10L);
        Routine routine = RoutineFixture.createRoutineWithId(member, "루팀이름",2L);
        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistoryWithId(member, routine, 1L);
        PerformedExercise performedExercise = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory, exercise);
        PerformedExerciseSet performedExerciseSet = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise);
        performedExercise.addPerformedExerciseSet(performedExerciseSet);
        exerciseHistory.addPerformedExercise(performedExercise);

        when(exerciseHistoryRepository.findByMemberIdAndPerformedDate(member.getId(), date))
                .thenReturn(Optional.of(exerciseHistory));

        Optional<ExerciseHistoryResponse> result = exerciseHistoryService.findByMemberIdAndPerformedDate(member.getId(), date);

        assertThat(result).isPresent();

        ExerciseHistoryResponse response = result.get();

        assertThat(response.routineId()).isEqualTo(routine.getId());
        assertThat(response.exerciseHistoryId()).isEqualTo(exerciseHistory.getId());
        assertThat(response.exerciseHistoryStatus()).isEqualTo(exerciseHistory.getExerciseHistoryStatus().toString());
        assertThat(response.routineName()).isEqualTo(routine.getName());

        assertThat(response.performedExerciseResponses()).hasSize(1);

        PerformedExerciseResponse exerciseResponse = response.performedExerciseResponses().get(0);
        assertThat(exerciseResponse.exerciseId()).isEqualTo(exercise.getId());
        assertThat(exerciseResponse.exerciseName()).isEqualTo(exercise.getName());
        assertThat(exerciseResponse.performedExerciseSetResponses()).hasSize(1);

        PerformedExerciseSetResponse setResponse = exerciseResponse.performedExerciseSetResponses().get(0);
        assertThat(setResponse.setCount()).isEqualTo(performedExerciseSet.getSetCount());
        assertThat(setResponse.weight()).isEqualTo(performedExerciseSet.getWeight());
        assertThat(setResponse.repetition()).isEqualTo(performedExerciseSet.getRepetition());
        assertThat(setResponse.completed()).isEqualTo(performedExerciseSet.getCompleted());
    }

    @Test
    void 세트가_존재하면_completed_상태를_토글한다() {
        Long setId = 1L;
        PerformedExerciseSet set = mock(PerformedExerciseSet.class);

        when(set.getCompleted()).thenReturn(true);
        when(performedExerciseSetRepository.findById(setId)).thenReturn(Optional.of(set));

        exerciseHistoryService.checkSet(setId);

        verify(set).updateCompleted(false);
    }

    @Test
    void 세트가_존재하지_않으면_예외를_던진다() {
        Long invalidId = 999L;
        when(performedExerciseSetRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseHistoryService.checkSet(invalidId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("루틴 수행 기록 세트 정보를 찾을 수 없습니다");
    }

    @Test
    void 부위별_일주일간_세트수를_집계해서_Enum_전체를_리턴한다() {

        Long memberId = 1L;

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        List<ExercisePartSetCountDto> dbResult = List.of(
                new ExercisePartSetCountDto(ExercisePart.CHEST, 5L),
                new ExercisePartSetCountDto(ExercisePart.BACK, 3L)
        );

        when(exerciseHistoryRepository.countSetPerExercisePart(memberId, startDate, endDate))
                .thenReturn(dbResult);

        List<ExercisePartSetCountDto> result = exerciseHistoryService.getWeeklySetCountByExercisePart(memberId);

        assertThat(result).hasSize(ExercisePart.values().length);

        for (ExercisePartSetCountDto dto : result) {
            if (dto.getExercisePart() == ExercisePart.CHEST) {
                assertThat(dto.getSetCount()).isEqualTo(5L);
            } else if (dto.getExercisePart() == ExercisePart.BACK) {
                assertThat(dto.getSetCount()).isEqualTo(3L);
            } else {
                assertThat(dto.getSetCount()).isEqualTo(0L); // 나머지는 기본값
            }
        }
    }

    @Test
    void 이번달과_저번달의_운동부위별_볼륨을_비교한다() {

        Long memberId = 1L;

        LocalDate now = LocalDate.now();
        LocalDate thisMonthStart = now.withDayOfMonth(1);
        LocalDate thisMonthEnd = now.withDayOfMonth(now.lengthOfMonth());

        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

        MonthlyPartVolumeDto chestThisMonth = mock(MonthlyPartVolumeDto.class);
        when(chestThisMonth.getPart()).thenReturn(ExercisePart.CHEST);
        when(chestThisMonth.getTotalVolume()).thenReturn(100.0);

        MonthlyPartVolumeDto backThisMonth = mock(MonthlyPartVolumeDto.class);
        when(backThisMonth.getPart()).thenReturn(ExercisePart.BACK);
        when(backThisMonth.getTotalVolume()).thenReturn(50.0);

        MonthlyPartVolumeDto chestLastMonth = mock(MonthlyPartVolumeDto.class);
        when(chestLastMonth.getPart()).thenReturn(ExercisePart.CHEST);
        when(chestLastMonth.getTotalVolume()).thenReturn(80.0);

        List<MonthlyPartVolumeDto> thisMonthData = List.of(chestThisMonth, backThisMonth);
        List<MonthlyPartVolumeDto> lastMonthData = List.of(chestLastMonth);

        when(exerciseHistoryRepository.findMonthlyVolumeByPart(memberId, thisMonthStart, thisMonthEnd))
                .thenReturn(thisMonthData);

        when(exerciseHistoryRepository.findMonthlyVolumeByPart(memberId, lastMonthStart, lastMonthEnd))
                .thenReturn(lastMonthData);

        List<PartVolumeComparisonDto> result = exerciseHistoryService.compareThisMonthAndLastMonthVolume(memberId);

        assertThat(result).hasSize(ExercisePart.values().length);

        for (PartVolumeComparisonDto dto : result) {
            if (dto.part() == ExercisePart.CHEST) {
                assertThat(dto.thisMonthVolume()).isEqualTo(100.0);
                assertThat(dto.lastMonthVolume()).isEqualTo(80.0);
            } else if (dto.part() == ExercisePart.BACK) {
                assertThat(dto.thisMonthVolume()).isEqualTo(50.0);
                assertThat(dto.lastMonthVolume()).isEqualTo(0.0);
            } else {
                assertThat(dto.thisMonthVolume()).isEqualTo(0.0);
                assertThat(dto.lastMonthVolume()).isEqualTo(0.0);
            }
        }
    }

    @Test
    void 부위별_운동상위5개를_정상적으로_그룹핑하여_리턴한다() {
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 21);

        TopExerciseDto chest1 = mock(TopExerciseDto.class);
        when(chest1.getExerciseName()).thenReturn("벤치프레스");

        TopExerciseDto chest2 = mock(TopExerciseDto.class);
        when(chest2.getExerciseName()).thenReturn("chest2");

        TopExerciseDto chest3 = mock(TopExerciseDto.class);
        when(chest3.getExerciseName()).thenReturn("chest3");

        List<TopExerciseDto> chestList = List.of(chest1, chest2, chest3);

        when(exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.CHEST.name())).thenReturn(chestList);
        when(exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.BACK.name())).thenReturn(List.of());
        when(exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.SHOULDERS.name())).thenReturn(List.of());
        when(exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.ARMS.name())).thenReturn(List.of());
        when(exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.CORE.name())).thenReturn(List.of());
        when(exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.LEGS.name())).thenReturn(List.of());
        when(exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.HIP.name())).thenReturn(List.of());

        TopExerciseResponse response = exerciseHistoryService.getTop5ExercisesByPart(startDate, endDate);

        assertThat(response.chests()).hasSize(3);
        assertThat(response.chests().get(0).getExerciseName()).isEqualTo("벤치프레스");
        assertThat(response.chests().get(1).getExerciseName()).isEqualTo("chest2");
        assertThat(response.chests().get(2).getExerciseName()).isEqualTo("chest3");

        assertThat(response.backs()).isEmpty();
        assertThat(response.shoulders()).isEmpty();
        assertThat(response.arms()).isEmpty();
        assertThat(response.cores()).isEmpty();
        assertThat(response.legs()).isEmpty();
        assertThat(response.hips()).isEmpty();
    }

    @Test
    void 주간_운동_리포트를_정상적으로_생성한다() throws Exception {

        Member member = MemberFixture.createMemberWithId(1L);
        LocalDate start = LocalDate.of(2024, 6, 1);
        LocalDate end = LocalDate.of(2024, 6, 7);

        PerformedExerciseSet set1 = mock(PerformedExerciseSet.class);
        when(set1.getRepetition()).thenReturn(10);
        when(set1.getWeight()).thenReturn(100.0f);

        PerformedExerciseSet set2 = mock(PerformedExerciseSet.class);
        when(set2.getRepetition()).thenReturn(8);
        when(set2.getWeight()).thenReturn(80.0f);

        Exercise exercise = mock(Exercise.class);
        when(exercise.getName()).thenReturn("벤치프레스");

        PerformedExercise performedExercise = mock(PerformedExercise.class);
        when(performedExercise.getExercise()).thenReturn(exercise);
        when(performedExercise.getPerformedExerciseSets()).thenReturn(List.of(set1, set2));

        ExerciseHistory history = mock(ExerciseHistory.class);
        when(history.getPerformedDate()).thenReturn(LocalDate.of(2024, 6, 1));
        when(history.getPerformedTime()).thenReturn(LocalTime.of(1, 30));
        when(history.getPerformedExercises()).thenReturn(List.of(performedExercise));

        when(exerciseHistoryRepository.findByMemberAndPerformedDateBetweenAndExerciseHistoryStatus(
                member, start, end, ExerciseHistoryStatus.COMPLETED))
                .thenReturn(List.of(history));

        CompletableFuture<WeeklyReportDto> future = exerciseHistoryService.generateAllWeeklyReports(member, start, end);
        WeeklyReportDto report = future.get();

        assertThat(report.getMemberId()).isEqualTo(member.getId());
        assertThat(report.getTotalWorkoutDays()).isEqualTo(1);
        assertThat(report.getTotalSetsSum()).isEqualTo(2);
        assertThat(report.getTotalRepsSum()).isEqualTo(18);
        assertThat(report.getTotalWeightSum()).isEqualTo(100.0 * 10 + 80.0 * 8);
        assertThat(report.getAverageWorkoutDurationMinutes()).isEqualTo(90);
        assertThat(report.getMostFrequentExercise()).isEqualTo("벤치프레스");

        assertThat(report.getExerciseSummaries()).hasSize(1);
        WeeklyReportDto.ExerciseSummary summary = report.getExerciseSummaries().get(0);
        assertThat(summary.getExerciseName()).isEqualTo("벤치프레스");
        assertThat(summary.getTotalSets()).isEqualTo(2);
        assertThat(summary.getTotalReps()).isEqualTo(18);
        assertThat(summary.getTotalWeight()).isEqualTo(1640.0);
    }

}