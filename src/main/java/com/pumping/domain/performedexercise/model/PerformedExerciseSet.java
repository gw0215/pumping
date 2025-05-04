package com.pumping.domain.performedexercise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
public class PerformedExerciseSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PerformedExercise performedExercise;

    private Float weight;

    private Integer repetition;

    private Integer setCount;

    private Boolean completed;

    public PerformedExerciseSet(PerformedExercise performedExercise, Float weight, Integer repetition, Integer setCount, Boolean completed) {
        this.performedExercise = performedExercise;
        this.weight = weight;
        this.repetition = repetition;
        this.setCount = setCount;
        this.completed = completed;
    }

    public void updateWeight(Float weight) {
        this.weight = weight;
    }

    public void updateRepetition(Integer repetition) {
        this.repetition = repetition;
    }

    public void updateSetCount(Integer setCount) {
        this.setCount = setCount;
    }

    public void updateCompleted(Boolean completed) {
        this.completed = completed;
    }

}
