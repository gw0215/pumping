package com.pumping.domain.performedexercise.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PerformedExerciseRequest {

    private Long exerciseId;
    private Integer exerciseOrder;
    private List<PerformedExerciseSetRequest> performedExerciseSetRequests;

    public PerformedExerciseRequest(Long exerciseId, Integer exerciseOrder, List<PerformedExerciseSetRequest> performedExerciseSetRequests) {
        this.exerciseId = exerciseId;
        this.exerciseOrder = exerciseOrder;
        this.performedExerciseSetRequests = performedExerciseSetRequests;
    }
}
