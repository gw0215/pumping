package com.pumping.domain.routine.dto;

import com.pumping.domain.routineexercise.dto.ExerciseSetRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RoutineExerciseRequest {

    private Long exerciseId;

    private Integer order;

    private List<ExerciseSetRequest> exerciseSetRequests;

    public RoutineExerciseRequest(Long exerciseId, Integer order, List<ExerciseSetRequest> exerciseSetRequests) {
        this.exerciseId = exerciseId;
        this.order = order;
        this.exerciseSetRequests = exerciseSetRequests;
    }
}
