package com.pumping.domain.performedexercise.dto;

import java.util.List;

public record PerformedExerciseResponse(Long performedExerciseId, Long exerciseId, String exerciseName, List<PerformedExerciseSetResponse> performedExerciseSetResponses) {
}
