package com.pumping.domain.performedroutine.model;

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
public class PerformedRoutine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Routine routine;

    private LocalTime performedTime;

    private PerformedRoutineStatus performedRoutineStatus;

    private LocalDate performedDate;

    @OneToMany(mappedBy = "performedRoutine")
    private List<PerformedExerciseSet> performedExerciseSets = new ArrayList<>();

    public PerformedRoutine(Routine routine, LocalTime performedTime, PerformedRoutineStatus performedRoutineStatus, LocalDate performedDate) {
        this.routine = routine;
        this.performedTime = performedTime;
        this.performedRoutineStatus = performedRoutineStatus;
        this.performedDate = performedDate;
    }

    public void updatePerformedRoutineStatus(PerformedRoutineStatus performedRoutineStatus) {
        this.performedRoutineStatus = performedRoutineStatus;
    }

    public void updatePerformedTime(LocalTime performedTime) {
        this.performedTime = performedTime;
    }

    public void addPerformedExerciseSet(PerformedExerciseSet performedExerciseSet) {
        performedExerciseSets.add(performedExerciseSet);
    }

}
