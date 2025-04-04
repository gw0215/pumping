package com.pumping.domain.routine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExerciseSetRequest {

    private Integer weight;

    private Integer repetition;

    private Integer setCount;

    public ExerciseSetRequest(Integer weight, Integer repetition, Integer setCount) {
        this.weight = weight;
        this.repetition = repetition;
        this.setCount = setCount;
    }
}
