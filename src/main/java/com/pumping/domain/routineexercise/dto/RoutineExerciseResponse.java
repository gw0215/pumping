package com.pumping.domain.routineexercise.dto;

import java.util.List;

public record RoutineExerciseResponse(Long exerciseId, String exerciseName, List<ExerciseSetResponse> exerciseSetResponses) {
}
