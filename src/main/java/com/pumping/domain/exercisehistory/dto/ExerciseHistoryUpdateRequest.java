package com.pumping.domain.exercisehistory.dto;

import com.pumping.domain.performedexerciseset.dto.PerformedExerciseSetRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ExerciseHistoryUpdateRequest {

    private List<PerformedExerciseSetRequest> performedExerciseSetRequests;

    public ExerciseHistoryUpdateRequest(List<PerformedExerciseSetRequest> performedExerciseSetRequests) {
        this.performedExerciseSetRequests = performedExerciseSetRequests;
    }
}
