package com.pumping.domain.routineexercise.fixture;

import com.pumping.domain.routineexercise.dto.ExerciseSetRequest;
import com.pumping.domain.routineexercise.model.ExerciseSet;
import com.pumping.domain.routineexercise.model.RoutineExercise;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ExerciseSetFixture {

    public static final Float WEIGHT = 5f;

    public static final Integer REPETITION = 10;

    public static final Integer SET_COUNT = 3;

    public static ExerciseSet createExerciseSet(RoutineExercise routineExercise) {
        return new ExerciseSet(routineExercise, WEIGHT, REPETITION, SET_COUNT);
    }

    public static ExerciseSetRequest createExerciseSetRequest() {
        return new ExerciseSetRequest(WEIGHT, REPETITION, SET_COUNT);
    }

    public static List<ExerciseSetRequest> createExerciseSetRequests(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createExerciseSetRequest())
                .collect(Collectors.toList());
    }

}
