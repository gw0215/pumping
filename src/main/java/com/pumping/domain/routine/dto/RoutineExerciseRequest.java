package com.pumping.domain.routine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoutineExerciseRequest {

    private Long exerciseId;

    private Integer weight;

    private Integer count;

    private Integer setCount;

    private Integer order;

    public RoutineExerciseRequest(Long exerciseId, Integer weight, Integer count, Integer setCount, Integer order) {
        this.exerciseId = exerciseId;
        this.weight = weight;
        this.count = count;
        this.setCount = setCount;
        this.order = order;
    }
}
