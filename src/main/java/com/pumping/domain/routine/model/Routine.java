package com.pumping.domain.routine.model;

import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import com.pumping.domain.exercisehistory.model.ExerciseHistoryStatus;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.performedexercise.model.PerformedExercise;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String name;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL)
    private List<RoutineExercise> routineExercises = new ArrayList<>();

    public Routine(Member member, String name) {
        this.member = member;
        this.name = name;
    }

    public void addRoutineExercise(RoutineExercise routineExercise) {
        routineExercises.add(routineExercise);
    }

    public ExerciseHistory createExerciseHistory(Member member, LocalDate date) {
        ExerciseHistory history = new ExerciseHistory(member, this, LocalTime.MIDNIGHT, ExerciseHistoryStatus.NOT_STARTED, date);

        for (RoutineExercise routineExercise : routineExercises) {
            PerformedExercise performedExercise = routineExercise.toPerformedExercise(history);
            history.addPerformedExercise(performedExercise);
        }

        return history;
    }
}
