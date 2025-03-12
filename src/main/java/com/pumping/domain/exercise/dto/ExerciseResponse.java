package com.pumping.domain.exercise.dto;

import lombok.Getter;

@Getter
public class ExerciseResponse {

    private String name;

    private String explain;

    private String part;

    public ExerciseResponse(String name, String explain, String part) {
        this.name = name;
        this.explain = explain;
        this.part = part;
    }
}
