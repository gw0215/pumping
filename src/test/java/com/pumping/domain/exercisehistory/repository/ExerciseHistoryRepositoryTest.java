package com.pumping.domain.exercisehistory.repository;

import com.pumping.domain.exercise.fixture.ExerciseFixture;
import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.model.ExercisePart;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.exercisehistory.dto.ExercisePartSetCountDto;
import com.pumping.domain.exercisehistory.fixture.ExerciseHistoryFixture;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import com.pumping.domain.exercisehistory.model.ExerciseHistoryStatus;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.domain.performedexercise.model.PerformedExercise;
import com.pumping.domain.performedexercise.model.PerformedExerciseSet;
import com.pumping.domain.routine.fixture.RoutineFixture;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExerciseHistoryRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    RoutineRepository routineRepository;

    @Autowired
    ExerciseHistoryRepository exerciseHistoryRepository;

    @Test
    void 사용자_아이디와_수행날짜로_운동기록_조회() {

        Member member = memberRepository.save(MemberFixture.createMember());

        Routine routine = RoutineFixture.createRoutine(member);
        routineRepository.save(routine);

        ExerciseHistory exerciseHistory = ExerciseHistoryFixture.createExerciseHistory(member, routine);
        exerciseHistoryRepository.save(exerciseHistory);

        Optional<ExerciseHistory> optionalExerciseHistory = exerciseHistoryRepository.findByMemberIdAndPerformedDate(member.getId(), exerciseHistory.getPerformedDate());

        Assertions.assertThat(optionalExerciseHistory).isPresent();
        Assertions.assertThat(optionalExerciseHistory.get()).isEqualTo(exerciseHistory);

    }

    @Test
    void 사용자_아이디와_수행날짜범위로_운동기록_조회() {

        Member member = memberRepository.save(MemberFixture.createMember());

        Routine routine = RoutineFixture.createRoutine(member);
        routineRepository.save(routine);

        Exercise exercise = ExerciseFixture.createExercise();
        exerciseRepository.save(exercise);

        ExerciseHistory exerciseHistory1 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().minusDays(1L), ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise1 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory1, exercise);
        PerformedExerciseSet performedExerciseSet1 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise1);
        performedExercise1.addPerformedExerciseSet(performedExerciseSet1);
        exerciseHistory1.addPerformedExercise(performedExercise1);
        exerciseHistoryRepository.save(exerciseHistory1);

        ExerciseHistory exerciseHistory2 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().minusDays(2L), ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise2 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory2, exercise);
        PerformedExerciseSet performedExerciseSet2 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise2);
        performedExercise2.addPerformedExerciseSet(performedExerciseSet2);
        exerciseHistory1.addPerformedExercise(performedExercise2);
        exerciseHistoryRepository.save(exerciseHistory2);

        ExerciseHistory exerciseHistory3 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().minusDays(3L), ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise3 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory3, exercise);
        PerformedExerciseSet performedExerciseSet3 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise3);
        performedExercise3.addPerformedExerciseSet(performedExerciseSet3);
        exerciseHistory1.addPerformedExercise(performedExercise3);
        exerciseHistoryRepository.save(exerciseHistory3);

        ExerciseHistory exerciseHistory4 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().plusDays(1L), ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise4 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory4, exercise);
        PerformedExerciseSet performedExerciseSet4 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise4);
        performedExercise4.addPerformedExerciseSet(performedExerciseSet4);
        exerciseHistory1.addPerformedExercise(performedExercise4);
        exerciseHistoryRepository.save(exerciseHistory4);

        ExerciseHistory exerciseHistory5 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().plusDays(2L), ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise5 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory5, exercise);
        PerformedExerciseSet performedExerciseSet5 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise5);
        performedExercise5.addPerformedExerciseSet(performedExerciseSet5);
        exerciseHistory1.addPerformedExercise(performedExercise5);
        exerciseHistoryRepository.save(exerciseHistory5);

        ExerciseHistory exerciseHistory6 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().plusDays(3L), ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise6 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory6, exercise);
        PerformedExerciseSet performedExerciseSet6 = ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise6);
        performedExercise6.addPerformedExerciseSet(performedExerciseSet6);
        exerciseHistory1.addPerformedExercise(performedExercise6);
        exerciseHistoryRepository.save(exerciseHistory6);

        List<WeeklyExerciseHistoryStatsDto> weeklyStats = exerciseHistoryRepository.findWeeklyStats(member.getId(), LocalDate.now().minusDays(4L), LocalDate.now().plusDays(4L));

        Assertions.assertThat(weeklyStats).hasSize(6);


    }

    @Test
    void 운동부위별_완료된_세트수_합계_조회() {

        int chestSetCount = 3;
        int backSetCount = 2;
        int dateOffset1 = 1;
        int dateOffset2 = 2;
        int dateRangeStart = 3;

        Member member = memberRepository.save(MemberFixture.createMember());
        Routine routine = routineRepository.save(RoutineFixture.createRoutine(member));
        Exercise chest = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.CHEST));
        Exercise back = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.BACK));

        LocalDate today = LocalDate.now();

        ExerciseHistory exerciseHistory1 = exerciseHistoryRepository.save(ExerciseHistoryFixture.createExerciseHistory(member, routine, today.minusDays(dateOffset1), ExerciseHistoryStatus.COMPLETED));
        PerformedExercise performedExercise1 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory1, chest);
        performedExercise1.addPerformedExerciseSet(ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise1, 3f, chestSetCount, true));
        exerciseHistory1.addPerformedExercise(performedExercise1);

        ExerciseHistory exerciseHistory2 = exerciseHistoryRepository.save(ExerciseHistoryFixture.createExerciseHistory(member, routine, today.minusDays(dateOffset2), ExerciseHistoryStatus.COMPLETED));
        PerformedExercise performedExercise2 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory2, back);
        performedExercise2.addPerformedExerciseSet(ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise2, 2f, backSetCount, true));
        exerciseHistory2.addPerformedExercise(performedExercise2);

        exerciseHistoryRepository.saveAll(List.of(exerciseHistory1, exerciseHistory2));

        List<ExercisePartSetCountDto> result = exerciseHistoryRepository.countSetPerExercisePart(member.getId(), today.minusDays(dateRangeStart), today);

        Assertions.assertThat(result).hasSize(2);

        Map<ExercisePart, Long> partToSetCount = result.stream().collect(Collectors.toMap(ExercisePartSetCountDto::getExercisePart, ExercisePartSetCountDto::getSetCount));

        Assertions.assertThat(partToSetCount.get(ExercisePart.CHEST)).isEqualTo(chestSetCount);
        Assertions.assertThat(partToSetCount.get(ExercisePart.BACK)).isEqualTo(backSetCount);
    }

    @Test
    void 월별_운동부위별_총_볼륨_조회() {

        float weight1 = 10f;
        int reps1 = 10;
        int sets1 = 3;
        double expectedVolumeThisMonth = weight1 * reps1 * sets1;

        float weight2 = 20f;
        int reps2 = 5;
        int sets2 = 2;

        Member member = memberRepository.save(MemberFixture.createMember());
        Routine routine = routineRepository.save(RoutineFixture.createRoutine(member));
        Exercise leg = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.LEGS));

        LocalDate thisMonthDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 10);
        LocalDate lastMonthDate = thisMonthDate.minusMonths(1).withDayOfMonth(10);

        ExerciseHistory exerciseHistory1 = ExerciseHistoryFixture.createExerciseHistory(member, routine, thisMonthDate, ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise1 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory1, leg);
        performedExercise1.addPerformedExerciseSet(ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise1, weight1, reps1, sets1));
        exerciseHistory1.addPerformedExercise(performedExercise1);

        ExerciseHistory exerciseHistory2 = ExerciseHistoryFixture.createExerciseHistory(member, routine, lastMonthDate, ExerciseHistoryStatus.COMPLETED);
        PerformedExercise performedExercise2 = ExerciseHistoryFixture.createPerformedExercise(exerciseHistory2, leg);
        performedExercise2.addPerformedExerciseSet(ExerciseHistoryFixture.createPerformedExerciseSet(performedExercise2, weight2, reps2, sets2));
        exerciseHistory2.addPerformedExercise(performedExercise2);

        exerciseHistoryRepository.saveAll(List.of(exerciseHistory1, exerciseHistory2));

        List<MonthlyPartVolumeDto> volumes = exerciseHistoryRepository.findMonthlyVolumeByPart(member.getId(), thisMonthDate.withDayOfMonth(1), thisMonthDate.withDayOfMonth(thisMonthDate.lengthOfMonth()));

        Assertions.assertThat(volumes).hasSize(1);

        MonthlyPartVolumeDto dto = volumes.get(0);
        Assertions.assertThat(dto.getPart()).isEqualTo(ExercisePart.LEGS);
        Assertions.assertThat(dto.getTotalVolume()).isEqualTo(expectedVolumeThisMonth);
    }


    @Test
    void 운동부위별_TOP5_운동명_조회() {

        Member member  = memberRepository.save(MemberFixture.createMember());
        Routine routine = routineRepository.save(RoutineFixture.createRoutine(member));

        Exercise chest1 = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.CHEST, "벤치프레스"));
        Exercise chest2 = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.CHEST, "딥스"));
        Exercise chest3 = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.CHEST, "체스트 플라이"));
        Exercise chest4 = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.CHEST, "푸시업"));

        Exercise back1 = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.BACK, "랫풀다운"));
        Exercise back2 = exerciseRepository.save(ExerciseFixture.createExercise(ExercisePart.BACK, "바벨로우"));

        LocalDate today = LocalDate.now();

        for (int i = 0; i < 3; i++) {
            ExerciseHistory eh = exerciseHistoryRepository.save(
                    ExerciseHistoryFixture.createExerciseHistory(
                            member, routine, today.minusDays(i), ExerciseHistoryStatus.COMPLETED));

            PerformedExercise pe = ExerciseHistoryFixture.createPerformedExercise(eh, chest1);
            pe.addPerformedExerciseSet(ExerciseHistoryFixture.createPerformedExerciseSet(pe));
            eh.addPerformedExercise(pe);
        }

        for (int i = 0; i < 2; i++) {
            ExerciseHistory eh = exerciseHistoryRepository.save(
                    ExerciseHistoryFixture.createExerciseHistory(
                            member, routine, today.minusDays(3 + i), ExerciseHistoryStatus.COMPLETED));

            PerformedExercise pe = ExerciseHistoryFixture.createPerformedExercise(eh, chest2);
            pe.addPerformedExerciseSet(ExerciseHistoryFixture.createPerformedExerciseSet(pe));
            eh.addPerformedExercise(pe);
        }

        ExerciseHistory ehChest3 = exerciseHistoryRepository.save(
                ExerciseHistoryFixture.createExerciseHistory(
                        member, routine, today.minusDays(5), ExerciseHistoryStatus.COMPLETED));
        PerformedExercise peChest3 = ExerciseHistoryFixture.createPerformedExercise(ehChest3, chest3);
        peChest3.addPerformedExerciseSet(ExerciseHistoryFixture.createPerformedExerciseSet(peChest3));
        ehChest3.addPerformedExercise(peChest3);

        ExerciseHistory ehChest4 = exerciseHistoryRepository.save(
                ExerciseHistoryFixture.createExerciseHistory(
                        member, routine, today.minusDays(6), ExerciseHistoryStatus.COMPLETED));
        PerformedExercise peChest4 = ExerciseHistoryFixture.createPerformedExercise(ehChest4, chest4);
        peChest4.addPerformedExerciseSet(ExerciseHistoryFixture.createPerformedExerciseSet(peChest4));
        ehChest4.addPerformedExercise(peChest4);

        for (int i = 0; i < 2; i++) {
            ExerciseHistory eh = exerciseHistoryRepository.save(
                    ExerciseHistoryFixture.createExerciseHistory(
                            member, routine, today.minusDays(7 + i), ExerciseHistoryStatus.COMPLETED));

            PerformedExercise pe = ExerciseHistoryFixture.createPerformedExercise(eh, back1);
            pe.addPerformedExerciseSet(ExerciseHistoryFixture.createPerformedExerciseSet(pe));
            eh.addPerformedExercise(pe);
        }

        ExerciseHistory ehBack2 = exerciseHistoryRepository.save(
                ExerciseHistoryFixture.createExerciseHistory(
                        member, routine, today.minusDays(9), ExerciseHistoryStatus.COMPLETED));
        PerformedExercise peBack2 = ExerciseHistoryFixture.createPerformedExercise(ehBack2, back2);
        peBack2.addPerformedExerciseSet(ExerciseHistoryFixture.createPerformedExerciseSet(peBack2));
        ehBack2.addPerformedExercise(peBack2);

        List<TopExerciseDto> result = exerciseHistoryRepository.findTop5ByPart(
                today.minusDays(10), today.plusDays(1));

        Assertions.assertThat(result).hasSize(6);

        Assertions.assertThat(result.get(0).getPart()).isEqualTo(ExercisePart.BACK);
        Assertions.assertThat(result.get(1).getPart()).isEqualTo(ExercisePart.BACK);
        Assertions.assertThat(result.get(2).getPart()).isEqualTo(ExercisePart.CHEST);
        Assertions.assertThat(result.get(3).getPart()).isEqualTo(ExercisePart.CHEST);
        Assertions.assertThat(result.get(4).getPart()).isEqualTo(ExercisePart.CHEST);
        Assertions.assertThat(result.get(5).getPart()).isEqualTo(ExercisePart.CHEST);

        Assertions.assertThat(result.get(0).getExerciseName()).isEqualTo("랫풀다운");
        Assertions.assertThat(result.get(0).getCnt()).isEqualTo(2);

        Assertions.assertThat(result.get(1).getExerciseName()).isEqualTo("바벨로우");
        Assertions.assertThat(result.get(1).getCnt()).isEqualTo(1);

        Assertions.assertThat(result.get(2).getExerciseName()).isEqualTo("벤치프레스");
        Assertions.assertThat(result.get(2).getCnt()).isEqualTo(3);

        Assertions.assertThat(result.get(3).getExerciseName()).isEqualTo("딥스");
        Assertions.assertThat(result.get(3).getCnt()).isEqualTo(2);

        Assertions.assertThat(result.get(4).getExerciseName()).isEqualTo("체스트 플라이");
        Assertions.assertThat(result.get(4).getCnt()).isEqualTo(1);

        Assertions.assertThat(result.get(5).getExerciseName()).isEqualTo("푸시업");
        Assertions.assertThat(result.get(5).getCnt()).isEqualTo(1);
    }

}