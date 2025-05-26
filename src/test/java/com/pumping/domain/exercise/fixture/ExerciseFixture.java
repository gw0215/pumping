package com.pumping.domain.exercise.fixture;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.model.ExercisePart;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ExerciseFixture {

    private static final String NAME = "벤치 프레스";
    private static final String EXPLAIN = "벤치 프레스는 가슴운동입니다.";
    private static final ExercisePart EXERCISE_PART = ExercisePart.CHEST;

    public static Exercise createExercise() {
        return new Exercise(NAME, EXPLAIN, EXERCISE_PART);
    }

    public static Exercise createExercise(ExercisePart exercisePart) {
        return new Exercise(NAME, EXPLAIN, exercisePart);
    }
    public static Exercise createExercise(ExercisePart exercisePart, String name) {
        return new Exercise(name, EXPLAIN, exercisePart);
    }

    public static List<Exercise> createExercises(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createExercise())
                .collect(Collectors.toList());
    }

}
