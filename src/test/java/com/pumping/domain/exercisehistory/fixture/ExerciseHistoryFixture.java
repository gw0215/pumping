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
import org.springframework.test.util.ReflectionTestUtils;

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

    public static ExerciseHistory createExerciseHistoryWithId(Member member, Routine routine,Long id) {
        return new ExerciseHistory(member, routine, LOCAL_TIME, ExerciseHistoryStatus.IN_PROGRESS, DATE);
    }

    public static ExerciseHistory createExerciseHistory(Member member, Routine routine, LocalDate date,ExerciseHistoryStatus exerciseHistoryStatus) {
        return new ExerciseHistory(member, routine, LOCAL_TIME, exerciseHistoryStatus, date);
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

    public static PerformedExercise createPerformedExerciseWithId(ExerciseHistory exerciseHistory, Exercise exercise,Long id) {
        PerformedExercise performedExercise = new PerformedExercise(exerciseHistory, exercise, SET_ORDER);
        ReflectionTestUtils.setField(performedExercise, "id", id);
        return performedExercise;
    }


    public static PerformedExerciseSet createPerformedExerciseSet(PerformedExercise performedExercise) {
        return new PerformedExerciseSet(performedExercise, WEIGHT, REPETITION, SET_COUNT, COMPLETED);
    }

    public static PerformedExerciseSet createPerformedExerciseSetWithId(PerformedExercise performedExercise,Long id) {
        PerformedExerciseSet performedExerciseSet = new PerformedExerciseSet(performedExercise, WEIGHT, REPETITION, SET_COUNT, COMPLETED);
        ReflectionTestUtils.setField(performedExerciseSet, "id", id);
        return performedExerciseSet;
    }

    public static PerformedExerciseSet createPerformedExerciseSet(PerformedExercise performedExercise, Float weight, Integer setCount, Boolean completed) {
        return new PerformedExerciseSet(performedExercise, weight, REPETITION, setCount, completed);
    }

    public static PerformedExerciseSet createPerformedExerciseSet(PerformedExercise pe, Float weight, Integer repetition, Integer setCount) {
        return new PerformedExerciseSet(pe, weight, repetition, setCount, true);
    }

    public static PerformedExerciseRequest createPerformedExerciseRequest(Long exerciseId, List<PerformedExerciseSetRequest> performedExerciseSetRequests) {
        return new PerformedExerciseRequest(exerciseId, EXERCISE_ORDER, performedExerciseSetRequests);
    }

    public static List<PerformedExerciseRequest> createPerformedExerciseRequests(int count,Long exerciseId, List<PerformedExerciseSetRequest> performedExerciseSetRequests) {
        return IntStream.range(0, count).mapToObj(i -> createPerformedExerciseRequest(exerciseId, performedExerciseSetRequests)).toList();
    }

}
