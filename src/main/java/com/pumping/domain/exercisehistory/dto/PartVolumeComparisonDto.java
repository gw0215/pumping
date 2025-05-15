package com.pumping.domain.exercisehistory.dto;

import com.pumping.domain.exercise.model.ExercisePart;

public record PartVolumeComparisonDto(ExercisePart part, Double thisMonthVolume, Double lastMonthVolume) {

}
