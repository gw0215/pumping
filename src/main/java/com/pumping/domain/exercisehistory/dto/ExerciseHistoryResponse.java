package com.pumping.domain.exercisehistory.dto;

import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;

import java.util.List;

public record ExerciseHistoryResponse(Long routineId, Long routineDateId, String performedRoutineStatus, String name, List<RoutineExerciseResponse> exercises) {
}
