package com.pumping.domain.exercisehistory.repository;

import com.pumping.domain.exercisehistory.fixture.ExerciseHistoryFixture;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
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
import java.util.Optional;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExerciseHistoryRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

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

        ExerciseHistory exerciseHistory1 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().minusDays(1L));
        exerciseHistoryRepository.save(exerciseHistory1);
        ExerciseHistory exerciseHistory2 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().minusDays(2L));
        exerciseHistoryRepository.save(exerciseHistory2);
        ExerciseHistory exerciseHistory3 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().minusDays(3L));
        exerciseHistoryRepository.save(exerciseHistory3);
        ExerciseHistory exerciseHistory4 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().plusDays(1L));
        exerciseHistoryRepository.save(exerciseHistory4);
        ExerciseHistory exerciseHistory5 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().plusDays(2L));
        exerciseHistoryRepository.save(exerciseHistory5);
        ExerciseHistory exerciseHistory6 = ExerciseHistoryFixture.createExerciseHistory(member, routine, LocalDate.now().plusDays(3L));
        exerciseHistoryRepository.save(exerciseHistory6);

        List<ExerciseHistory> exerciseHistories = exerciseHistoryRepository.findByMemberAndPerformedDateBetween(member, LocalDate.now().minusDays(4L), LocalDate.now().plusDays(4L));

        Assertions.assertThat(exerciseHistories).hasSize(6);


    }

}