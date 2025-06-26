package com.pumping.domain.routineexercise.fixture;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.routineexercise.dto.ExerciseSetRequest;
import com.pumping.domain.routine.dto.RoutineExerciseRequest;
import com.pumping.domain.routine.dto.RoutineExerciseRequests;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routineexercise.model.RoutineExercise;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class RoutineExerciseFixture {

    public static RoutineExercise createRoutineExercise(Routine routine, Exercise exercise) {
        return new RoutineExercise(routine, exercise, 1);
    }

    public static RoutineExerciseRequest createRoutineExerciseRequest(Long exerciseId, List<ExerciseSetRequest> exerciseSetRequests) {
        return new RoutineExerciseRequest(exerciseId, 1, exerciseSetRequests);
    }

    public static List<RoutineExerciseRequest> createRoutineExerciseRequests(Long exerciseId, int count, List<ExerciseSetRequest> exerciseSetRequests) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    return new RoutineExerciseRequest(exerciseId, i + 1, exerciseSetRequests);
                })
                .collect(Collectors.toList());
    }

    public static RoutineExerciseRequests createRoutineExerciseRequests(String routineName,List<RoutineExerciseRequest> routineExerciseRequests) {
        return new RoutineExerciseRequests(routineName, routineExerciseRequests);
    }
}
