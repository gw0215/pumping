package com.pumping.domain.routine.dto;

import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;

import java.util.List;

public record RoutineDetailResponse(Long id, String name, List<RoutineExerciseResponse> exercises) {
}
