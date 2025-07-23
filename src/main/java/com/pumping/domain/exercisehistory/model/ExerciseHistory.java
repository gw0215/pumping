package com.pumping.domain.exercisehistory.model;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.performedexercise.dto.PerformedExerciseRequest;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetRequest;
import com.pumping.domain.performedexercise.model.PerformedExercise;
import com.pumping.domain.performedexercise.model.PerformedExerciseSet;
import com.pumping.domain.routine.model.Routine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(indexes = {
        @Index(name = "idx_eh_status_date", columnList = "exercise_history_status, performed_date")
})
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

    @Enumerated(EnumType.STRING)
    private ExerciseHistoryStatus exerciseHistoryStatus;

    private LocalDate performedDate;

    @OneToMany(mappedBy = "exerciseHistory",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PerformedExercise> performedExercises = new ArrayList<>();

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

    public void addPerformedExercise(PerformedExercise performedExercise) {
        performedExercises.add(performedExercise);
    }


    public void deletePerformedSets(List<Long> setIdsToDelete) {
        for (PerformedExercise pe : performedExercises) {
            pe.deleteSetsByIds(setIdsToDelete);
        }
    }

    public void addPerformedSets(List<PerformedExerciseSetRequest> addRequests) {
        Map<Long, PerformedExercise> map = performedExercises.stream()
                .collect(Collectors.toMap(PerformedExercise::getId, pe -> pe));

        for (PerformedExerciseSetRequest req : addRequests) {
            PerformedExercise pe = map.get(req.getPerformedExerciseId());
            if (pe != null) {
                pe.addPerformedSet(new PerformedExerciseSet(pe, req.getWeight(), req.getRepetition(), req.getSetCount(), req.getCompleted()));
            }
        }
    }

    public void updatePerformedSets(List<PerformedExerciseSetRequest> updateRequests) {
        for (PerformedExercise pe : performedExercises) {
            pe.updateSets(updateRequests);
        }
    }

    public void addNewExercises(List<PerformedExerciseRequest> requests, ExerciseRepository exerciseRepository) {
        for (PerformedExerciseRequest req : requests) {
            Exercise exercise = exerciseRepository.findById(req.getExerciseId())
                    .orElseThrow(() -> new EntityNotFoundException("운동 없음"));

            PerformedExercise pe = new PerformedExercise(this, exercise, req.getExerciseOrder());
            for (PerformedExerciseSetRequest setReq : req.getPerformedExerciseSetRequests()) {
                pe.addPerformedSet(new PerformedExerciseSet(pe, setReq.getWeight(), setReq.getRepetition(), setReq.getSetCount(), setReq.getCompleted()));
            }
            this.addPerformedExercise(pe);
        }
    }


}
