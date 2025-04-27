package com.pumping.domain.exercisehistory.dto;

import java.util.List;

public record ExerciseHistoryWeekStatusResponse(
        long totalSeconds,
        float totalVolume,
        List<Long> totalSecondsPerDay,
        List<Float> totalVolumePerDay
) { }
