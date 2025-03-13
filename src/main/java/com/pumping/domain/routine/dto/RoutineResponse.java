package com.pumping.domain.routine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoutineResponse {

    private Long id;
    private String name;

    public RoutineResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
