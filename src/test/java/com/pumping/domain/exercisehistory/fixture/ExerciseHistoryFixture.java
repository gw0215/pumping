package com.pumping.domain.exercisehistory.fixture;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryUpdateRequest;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.exercisehistory.model.ExerciseHistoryStatus;
import com.pumping.domain.performedexercise.dto.PerformedExerciseRequest;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetRequest;
import com.pumping.domain.performedexercise.model.PerformedExercise;
import com.pumping.domain.performedexercise.model.PerformedExerciseSet;
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

    public static Integer SET_ORDER = 1;

    public static Boolean COMPLETED = false;

    public static Integer EXERCISE_ORDER = 1;

    public static ExerciseHistoryRequest createRoutineDateRequest(Long routineId) {
        return new ExerciseHistoryRequest(routineId, DATE);
    }

    public static ExerciseHistory createExerciseHistory(Member member, Routine routine) {
        return new ExerciseHistory(member, routine, LOCAL_TIME, ExerciseHistoryStatus.IN_PROGRESS, DATE);
    }

    public static ExerciseHistory createExerciseHistory(Member member, Routine routine, LocalDate date) {
        return new ExerciseHistory(member, routine, LOCAL_TIME, ExerciseHistoryStatus.IN_PROGRESS, date);
    }

    public static ExerciseHistoryUpdateRequest createExerciseHistoryUpdateRequest(List<PerformedExerciseSetRequest> addedSets,List<PerformedExerciseSetRequest> updatedSets,List<Long> deletedSetIds,List<PerformedExerciseRequest> newExercises) {
        return new ExerciseHistoryUpdateRequest(addedSets,updatedSets,newExercises,deletedSetIds);
    }

    public static PerformedExerciseSetRequest createPerformedExerciseSetRequest(Long performedExerciseSetId, Long performedExerciseId) {
        return new PerformedExerciseSetRequest(performedExerciseSetId, performedExerciseId, WEIGHT, REPETITION, SET_COUNT, false);
    }

    public static List<PerformedExerciseSetRequest> createPerformedExerciseSetRequests(int count,Long performedExerciseSetId, Long performedExerciseId) {
        return IntStream.range(0, count).mapToObj(i -> createPerformedExerciseSetRequest(performedExerciseSetId, performedExerciseId)).toList();
    }

    public static PerformedExercise createPerformedExercise(ExerciseHistory exerciseHistory, Exercise exercise) {
        return new PerformedExercise(exerciseHistory,exercise,SET_ORDER);
    }

    public static PerformedExerciseSet createPerformedExerciseSet(PerformedExercise performedExercise) {
        return new PerformedExerciseSet(performedExercise, WEIGHT, REPETITION, SET_COUNT, COMPLETED);
    }

    public static List<PerformedExerciseSet> createPerformedExerciseSet(int count,PerformedExercise performedExercise) {
        return IntStream.range(0, count).mapToObj(i -> createPerformedExerciseSet(performedExercise)).toList();
    }

    public static PerformedExerciseRequest createPerformedExerciseRequest(Long exerciseId, List<PerformedExerciseSetRequest> performedExerciseSetRequests) {
        return new PerformedExerciseRequest(exerciseId, EXERCISE_ORDER, performedExerciseSetRequests);
    }

    public static List<PerformedExerciseRequest> createPerformedExerciseRequests(int count,Long exerciseId, List<PerformedExerciseSetRequest> performedExerciseSetRequests) {
        return IntStream.range(0, count).mapToObj(i -> createPerformedExerciseRequest(exerciseId, performedExerciseSetRequests)).toList();
    }


}
