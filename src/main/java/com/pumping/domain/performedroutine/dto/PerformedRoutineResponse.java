package com.pumping.domain.performedroutine.dto;

import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;

import java.util.List;

public record PerformedRoutineResponse (Long routineId, Long routineDateId, String performedRoutineStatus, String name, List<RoutineExerciseResponse> exercises) {
}
