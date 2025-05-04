package com.pumping.domain.exercisehistory.dto;

import com.pumping.domain.performedexercise.dto.PerformedExerciseResponse;

import java.util.List;

public record ExerciseHistoryResponse(Long routineId, Long exerciseHistoryId, String exerciseHistoryStatus, String routineName, List<PerformedExerciseResponse> performedExerciseResponses) {
}
