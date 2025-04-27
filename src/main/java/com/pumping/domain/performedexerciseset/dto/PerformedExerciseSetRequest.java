package com.pumping.domain.performedexerciseset.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PerformedExerciseSetRequest {

    private Long exerciseId;

    private Float weight;

    private Integer repetition;

    private Integer setCount;

    private Boolean checked;

    public PerformedExerciseSetRequest(Long exerciseId, Float weight, Integer repetition, Integer setCount, Boolean checked) {
        this.exerciseId = exerciseId;
        this.weight = weight;
        this.repetition = repetition;
        this.setCount = setCount;
        this.checked = checked;
    }
}
