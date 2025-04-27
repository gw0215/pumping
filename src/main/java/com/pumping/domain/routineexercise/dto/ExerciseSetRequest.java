package com.pumping.domain.routineexercise.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExerciseSetRequest {

    private Float weight;

    private Integer repetition;

    private Integer setCount;

    public ExerciseSetRequest(Float weight, Integer repetition, Integer setCount) {
        this.weight = weight;
        this.repetition = repetition;
        this.setCount = setCount;
    }
}
