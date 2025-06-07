package com.pumping.domain.exercisehistory.repository;

import com.pumping.domain.exercise.model.ExercisePart;

public interface MonthlyPartVolumeDto {
    ExercisePart getPart();
    Double getTotalVolume();
}
