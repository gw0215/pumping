package com.pumping.domain.routineexercise.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExerciseSetDetail {

    private Integer set;
    private Integer weight;
    private Integer count;

    public ExerciseSetDetail(Integer set, Integer weight, Integer count) {
        this.set = set;
        this.weight = weight;
        this.count = count;
    }
}
