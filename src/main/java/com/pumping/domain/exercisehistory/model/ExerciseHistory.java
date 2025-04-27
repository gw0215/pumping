package com.pumping.domain.exercisehistory.model;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.performedexerciseset.model.PerformedExerciseSet;
import com.pumping.domain.routine.model.Routine;
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
public class ExerciseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Routine routine;

    private LocalTime performedTime;

    private ExerciseHistoryStatus exerciseHistoryStatus;

    private LocalDate performedDate;

    @OneToMany(mappedBy = "exerciseHistory",cascade = CascadeType.ALL)
    private List<PerformedExerciseSet> performedExerciseSets = new ArrayList<>();

    public ExerciseHistory(Member member, Routine routine, LocalTime performedTime, ExerciseHistoryStatus exerciseHistoryStatus, LocalDate performedDate) {
        this.member = member;
        this.routine = routine;
        this.performedTime = performedTime;
        this.exerciseHistoryStatus = exerciseHistoryStatus;
        this.performedDate = performedDate;
    }

    public void updatePerformedRoutineStatus(ExerciseHistoryStatus exerciseHistoryStatus) {
        this.exerciseHistoryStatus = exerciseHistoryStatus;
    }

    public void updatePerformedTime(LocalTime performedTime) {
        this.performedTime = performedTime;
    }

    public void addPerformedExerciseSet(PerformedExerciseSet performedExerciseSet) {
        performedExerciseSets.add(performedExerciseSet);
    }

    public void clearPerformedExerciseSet() {
        performedExerciseSets.clear();
    }

}
