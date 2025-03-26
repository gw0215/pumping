package com.pumping.domain.routineexercise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ExerciseSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    public RoutineExercise routineExercise;

    private Integer weight;

    private Integer count;

    private Integer setCount;

    public ExerciseSet(RoutineExercise routineExercise, Integer weight, Integer count, Integer setCount) {
        this.routineExercise = routineExercise;
        this.weight = weight;
        this.count = count;
        this.setCount = setCount;
    }
}
