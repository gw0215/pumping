package com.pumping.domain.exercisehistory.dto;

import com.pumping.domain.performedexercise.dto.PerformedExerciseRequest;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ExerciseHistoryUpdateRequest {

    private List<PerformedExerciseSetRequest> addedSets;
    private List<PerformedExerciseSetRequest> updatedSets;
    private List<PerformedExerciseRequest> newExercises;
    private List<Long> deletedSetIds;

    public ExerciseHistoryUpdateRequest(List<PerformedExerciseSetRequest> addedSets, List<PerformedExerciseSetRequest> updatedSets, List<PerformedExerciseRequest> newExercises, List<Long> deletedSetIds) {
        this.addedSets = addedSets;
        this.updatedSets = updatedSets;
        this.newExercises = newExercises;
        this.deletedSetIds = deletedSetIds;
    }
}
