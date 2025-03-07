package com.pumping.domain.routine.dto;

import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class RoutineResponse {

    private Long id;
    private String name;
    private List<RoutineExerciseResponse> exercises;

    public RoutineResponse(Long id, String name, List<RoutineExerciseResponse> exercises) {
        this.id = id;
        this.name = name;
        this.exercises = exercises;
    }
}
