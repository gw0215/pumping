package com.pumping.domain.exercisehistory.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class WeeklyReportDto {
    private final Long memberId;
    private final Long totalWorkoutDays;
    private final int totalSetsSum;
    private final int totalRepsSum;
    private final double totalWeightSum;
    private final int averageWorkoutDurationMinutes;
    private final String mostFrequentExercise;
    private final List<ExerciseSummary> exerciseSummaries;

    public WeeklyReportDto(
            Long memberId,
            Long totalWorkoutDays,
            int totalSetsSum,
            int totalRepsSum,
            double totalWeightSum,
            int averageWorkoutDurationMinutes,
            String mostFrequentExercise,
            List<ExerciseSummary> exerciseSummaries
    ) {
        this.memberId = memberId;
        this.totalWorkoutDays = totalWorkoutDays;
        this.totalSetsSum = totalSetsSum;
        this.totalRepsSum = totalRepsSum;
        this.totalWeightSum = totalWeightSum;
        this.averageWorkoutDurationMinutes = averageWorkoutDurationMinutes;
        this.mostFrequentExercise = mostFrequentExercise;
        this.exerciseSummaries = exerciseSummaries;
    }

    @Getter
    public static class ExerciseSummary {
        private final String exerciseName;
        private final int totalSets;
        private final int totalReps;
        private final double totalWeight;

        public ExerciseSummary(String exerciseName, int totalSets, int totalReps, double totalWeight) {
            this.exerciseName = exerciseName;
            this.totalSets = totalSets;
            this.totalReps = totalReps;
            this.totalWeight = totalWeight;
        }
    }
}