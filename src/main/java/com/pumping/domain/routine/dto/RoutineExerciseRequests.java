package com.pumping.domain.routine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RoutineExerciseRequests {

    private String routineName;

    private List<RoutineExerciseRequest> routineExerciseRequests;

    public RoutineExerciseRequests(String routineName, List<RoutineExerciseRequest> routineExerciseRequests) {
        this.routineName = routineName;
        this.routineExerciseRequests = routineExerciseRequests;
    }
}
