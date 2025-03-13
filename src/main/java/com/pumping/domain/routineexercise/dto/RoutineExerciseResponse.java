package com.pumping.domain.routineexercise.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RoutineExerciseResponse {

    private String exerciseName;

    private List<ExerciseSetDetail> exerciseSetDetails;

    public RoutineExerciseResponse(String exerciseName, List<ExerciseSetDetail> exerciseSetDetails) {
        this.exerciseName = exerciseName;
        this.exerciseSetDetails = exerciseSetDetails;
    }
}
