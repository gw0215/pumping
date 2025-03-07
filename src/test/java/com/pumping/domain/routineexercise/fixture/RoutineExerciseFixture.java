package com.pumping.domain.routineexercise.fixture;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.routine.dto.RoutineExerciseRequest;
import com.pumping.domain.routine.dto.RoutineExerciseRequests;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routineexercise.model.RoutineExercise;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class RoutineExerciseFixture {

    public static RoutineExercise createRoutineExercise(Routine routine, Exercise exercise) {
        return new RoutineExercise(routine, exercise, 10, 5);
    }

    public static RoutineExerciseRequest createRoutineExerciseRequest() {
        return new RoutineExerciseRequest(1L, 20, 10, 5);
    }

    public static List<RoutineExerciseRequest> createRoutineExerciseRequests(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createRoutineExerciseRequest())
                .collect(Collectors.toList());
    }

    public static RoutineExerciseRequests createRoutineExerciseRequests(List<RoutineExerciseRequest> routineExerciseRequests) {
        return new RoutineExerciseRequests("상체운동", routineExerciseRequests);
    }
}
