package com.pumping.domain.routineexercise.model;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.routine.model.Routine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    private Integer setOrder;

    @OneToMany
    private List<ExerciseSet> exerciseSets = new ArrayList<>();

    public RoutineExercise(Routine routine, Exercise exercise, Integer setOrder) {
        this.routine = routine;
        this.exercise = exercise;
        this.setOrder = setOrder;
    }

    public void addExerciseSet(ExerciseSet exerciseSet) {
        exerciseSets.add(exerciseSet);
    }
}
