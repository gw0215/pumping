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

    private Float weight;

    private Integer repetition;

    private Integer setCount;

    public ExerciseSet(RoutineExercise routineExercise, Float weight, Integer repetition, Integer setCount) {
        this.routineExercise = routineExercise;
        this.weight = weight;
        this.repetition = repetition;
        this.setCount = setCount;
    }
}
