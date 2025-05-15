package com.pumping.domain.exercisehistory.repository;

import java.time.LocalDate;

public interface WeeklyExerciseHistoryStatsDto {
    LocalDate getPerformedDate();
    Long getTotalSeconds();
    Float getTotalVolume();
}
