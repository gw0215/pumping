package com.pumping.domain.routinedate.model;

import com.pumping.domain.routine.model.Routine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class RoutineDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Routine routine;

    private LocalDate performedDate;

    public RoutineDate(Routine routine, LocalDate performedDate) {
        this.routine = routine;
        this.performedDate = performedDate;
    }
}
