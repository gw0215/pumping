package com.pumping.domain.exercisehistory.dto;

import com.pumping.domain.exercise.model.ExercisePart;
import lombok.Getter;

@Getter
public class ExercisePartSetCountDto {

    private final ExercisePart exercisePart;
    private final Long setCount;

    public ExercisePartSetCountDto(ExercisePart exercisePart, Long setCount) {
        this.exercisePart = exercisePart;
        this.setCount = setCount;
    }
}