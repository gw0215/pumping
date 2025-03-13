package com.pumping.domain.routineexercise.model;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.routine.model.Routine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RoutineExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    private Exercise exercise;

    private Integer weight;

    private Integer count;

    private Integer setCount;

    private Integer order;

    public RoutineExercise(Routine routine, Exercise exercise, Integer weight, Integer count, Integer setCount, Integer order) {
        this.routine = routine;
        this.exercise = exercise;
        this.weight = weight;
        this.count = count;
        this.setCount = setCount;
        this.order = order;
    }
}
