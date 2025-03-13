package com.pumping.domain.exercise.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExerciseResponse {

    private Long id;

    private String name;

    private String explain;

    private String part;

    public ExerciseResponse(Long id, String name, String explain, String part) {
        this.id = id;
        this.name = name;
        this.explain = explain;
        this.part = part;
    }
}
