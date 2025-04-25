package com.pumping.domain.performedexerciseset.model;

import com.pumping.domain.performedroutine.model.PerformedRoutine;
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
    public PerformedRoutine performedRoutine;

    private Integer weight;

    private Integer repetition;

    private Integer setCount;

    private Boolean completed;

    public PerformedExerciseSet(PerformedRoutine performedRoutine, Integer weight, Integer repetition, Integer setCount, Boolean completed) {
        this.performedRoutine = performedRoutine;
        this.weight = weight;
        this.repetition = repetition;
        this.setCount = setCount;
        this.completed = completed;
    }
}
