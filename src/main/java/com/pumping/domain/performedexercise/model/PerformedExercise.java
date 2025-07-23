package com.pumping.domain.performedexercise.model;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import com.pumping.domain.performedexercise.dto.PerformedExerciseResponse;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetRequest;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public void deleteSetsByIds(List<Long> ids) {
        performedExerciseSets.removeIf(set -> ids.contains(set.getId()));
    }

    public void addPerformedSet(PerformedExerciseSet set) {
        this.performedExerciseSets.add(set);
    }

    public void updateSets(List<PerformedExerciseSetRequest> performedExerciseSetRequests) {

        System.out.println("🔍 모든 PerformedExerciseSet ID 출력:");
        for (PerformedExerciseSet set : performedExerciseSets) {
            System.out.println("Set: " + set + ", ID: " + set.getId());
        }

        for (PerformedExerciseSet set : performedExerciseSets) {
            for (PerformedExerciseSetRequest performedExerciseSetRequest : performedExerciseSetRequests) {
                if (set.getId().equals(performedExerciseSetRequest.getPerformedExerciseSetId())) {
                    set.updateWeight(performedExerciseSetRequest.getWeight());
                    set.updateRepetition(performedExerciseSetRequest.getRepetition());
                    set.updateSetCount(performedExerciseSetRequest.getSetCount());
                    set.updateCompleted(performedExerciseSetRequest.getCompleted());
                }
            }
        }
    }

    public PerformedExerciseResponse toPerformedExerciseResponse() {
        List<PerformedExerciseSetResponse> setResponses = performedExerciseSets.stream()
                .map(set -> new PerformedExerciseSetResponse(
                        set.getId(),
                        set.getSetCount(),
                        set.getWeight(),
                        set.getRepetition(),
                        set.getCompleted()))
                .collect(Collectors.toList());

        return new PerformedExerciseResponse(
                id,
                exercise.getId(),
                exercise.getName(),
                setResponses
        );
    }


}
