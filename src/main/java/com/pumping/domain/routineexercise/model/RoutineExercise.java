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

    private Integer exerciseOrder;

    @OneToMany(mappedBy = "routineExercise", cascade = CascadeType.ALL)
    private List<ExerciseSet> exerciseSets = new ArrayList<>();

    public RoutineExercise(Routine routine, Exercise exercise, Integer exerciseOrder) {
        this.routine = routine;
        this.exercise = exercise;
        this.exerciseOrder = exerciseOrder;
    }

    public void addExerciseSet(ExerciseSet exerciseSet) {
        exerciseSets.add(exerciseSet);
    }
}
