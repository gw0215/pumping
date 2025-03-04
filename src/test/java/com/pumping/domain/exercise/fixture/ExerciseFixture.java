package com.pumping.domain.exercise.fixture;

import com.pumping.domain.exercise.model.Exercise;

public abstract class ExerciseFixture {

    private static final String NAME = "벤치 프레스";
    private static final String EXPLAIN = "벤치 프레스는 가슴운동입니다.";
    private static final String PART = "상체";

    public static Exercise createExercise() {
        return new Exercise(NAME, EXPLAIN, PART);
    }

}
