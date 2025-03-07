package com.pumping.domain.routineexercise.dto;

import lombok.Getter;

@Getter
public class RoutineExerciseResponse {

    private String exerciseName;
    private Integer weight;
    private Integer count;

    public RoutineExerciseResponse(String exerciseName, Integer weight, Integer count) {
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.count = count;
    }
}
