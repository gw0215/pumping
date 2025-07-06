package com.pumping.domain.performedexercise.model;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(indexes = {
        @Index(name = "idx_pe_history_exercise", columnList = "exercise_history_id, exercise_id")
})
@NoArgsConstructor
public class PerformedExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    public ExerciseHistory exerciseHistory;

    @ManyToOne
    public Exercise exercise;

    private Integer exerciseOrder;

    @OneToMany(mappedBy = "performedExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformedExerciseSet> performedExerciseSets = new ArrayList<>();

    public PerformedExercise(ExerciseHistory exerciseHistory, Exercise exercise, Integer exerciseOrder) {
        this.exerciseHistory = exerciseHistory;
        this.exercise = exercise;
        this.exerciseOrder = exerciseOrder;
    }

    public void addPerformedExerciseSet(PerformedExerciseSet performedExerciseSet) {
        performedExerciseSets.add(performedExerciseSet);
    }
}
