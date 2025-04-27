package com.pumping.domain.performedexerciseset.model;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
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

    @ManyToOne(fetch = FetchType.LAZY)
    public ExerciseHistory exerciseHistory;

    @ManyToOne
    public Exercise exercise;

    private Float weight;

    private Integer repetition;

    private Integer setCount;

    private Boolean checked;

    public PerformedExerciseSet(ExerciseHistory exerciseHistory, Exercise exercise, Float weight, Integer repetition, Integer setCount, Boolean checked) {
        this.exerciseHistory = exerciseHistory;
        this.exercise = exercise;
        this.weight = weight;
        this.repetition = repetition;
        this.setCount = setCount;
        this.checked = checked;
    }
}
