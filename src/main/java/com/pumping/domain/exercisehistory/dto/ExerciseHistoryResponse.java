package com.pumping.domain.exercisehistory.dto;

import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import com.pumping.domain.performedexercise.dto.PerformedExerciseResponse;
import com.pumping.domain.performedexercise.model.PerformedExercise;
import com.pumping.domain.routine.model.Routine;

import java.util.List;
import java.util.stream.Collectors;

public record ExerciseHistoryResponse(
        Long routineId,
        Long exerciseHistoryId,
        String exerciseHistoryStatus,
        String routineName,
        List<PerformedExerciseResponse> performedExerciseResponses
) {
    public static ExerciseHistoryResponse of(ExerciseHistory history) {
        Routine routine = history.getRoutine();

        List<PerformedExerciseResponse> responses = history.getPerformedExercises().stream()
                .map(PerformedExercise::toPerformedExerciseResponse)
                .collect(Collectors.toList());

        return new ExerciseHistoryResponse(
                routine.getId(),
                history.getId(),
                history.getExerciseHistoryStatus().toString(),
                routine.getName(),
                responses
        );
    }
}
