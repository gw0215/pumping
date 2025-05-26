package com.pumping.domain.exercisehistory.repository;

import com.pumping.domain.exercise.model.ExercisePart;

public interface TopExerciseDto {
    Long         getExerciseId();
    String       getExerciseName();
    ExercisePart getPart();
    Integer      getCnt();
}
