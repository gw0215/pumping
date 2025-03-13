package com.pumping.domain.routine.dto;

import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RoutineDetailResponse {

    private Long id;
    private String name;
    private List<RoutineExerciseResponse> exercises;

    public RoutineDetailResponse(Long id, String name, List<RoutineExerciseResponse> exercises) {
        this.id = id;
        this.name = name;
        this.exercises = exercises;
    }

}
