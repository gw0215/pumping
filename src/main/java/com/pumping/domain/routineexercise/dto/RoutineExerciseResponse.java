package com.pumping.domain.routineexercise.dto;

import java.util.List;

public record RoutineExerciseResponse(String exerciseName, List<ExerciseSetResponse> exerciseSetResponses) {
}
