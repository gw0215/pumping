package com.pumping.domain.exercisehistory.fixture;

import com.pumping.domain.exercisehistory.dto.ExerciseHistoryUpdateRequest;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.exercisehistory.model.ExerciseHistoryStatus;
import com.pumping.domain.performedexerciseset.dto.PerformedExerciseSetRequest;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryRequest;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

public abstract class ExerciseHistoryFixture {

    public static LocalDate DATE = LocalDate.now();

    public static LocalTime LOCAL_TIME = LocalTime.MIDNIGHT;

    public static Float WEIGHT = 1f;

    public static Integer REPETITION = 1;

    public static Integer SET_COUNT = 1;

    public static ExerciseHistoryRequest createRoutineDateRequest(Long routineId) {
        return new ExerciseHistoryRequest(routineId, DATE);
    }

    public static ExerciseHistory createExerciseHistory(Member member, Routine routine) {
        return new ExerciseHistory(member, routine, LOCAL_TIME, ExerciseHistoryStatus.IN_PROGRESS, DATE);
    }

    public static ExerciseHistoryUpdateRequest createExerciseHistoryUpdateRequest(List<PerformedExerciseSetRequest> performedExerciseSetRequests) {
        return new ExerciseHistoryUpdateRequest(performedExerciseSetRequests);
    }

    public static PerformedExerciseSetRequest createPerformedExerciseSetRequest(Long exerciseId) {
        return new PerformedExerciseSetRequest(exerciseId, WEIGHT, REPETITION, SET_COUNT,false);
    }

    public static List<PerformedExerciseSetRequest> createPerformedExerciseSetRequests(int count,Long exerciseId) {
        return IntStream.range(0, count).mapToObj(i-> createPerformedExerciseSetRequest(exerciseId)).toList();
    }

}
