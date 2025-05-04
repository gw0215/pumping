package com.pumping.domain.performedexercise.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PerformedExerciseSetRequest {

    private Long performedExerciseSetId;

    private Long performedExerciseId;

    private Float weight;

    private Integer repetition;

    private Integer setCount;

    private Boolean completed;

    public PerformedExerciseSetRequest(Long performedExerciseSetId, Long performedExerciseId, Float weight, Integer repetition, Integer setCount, Boolean completed) {
        this.performedExerciseSetId = performedExerciseSetId;
        this.performedExerciseId = performedExerciseId;
        this.weight = weight;
        this.repetition = repetition;
        this.setCount = setCount;
        this.completed = completed;
    }
}
