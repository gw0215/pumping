package com.pumping.domain.performedexercise.dto;

public record PerformedExerciseSetResponse (Long performedExerciseSetId, Integer setCount, Float weight, Integer repetition,Boolean completed) {
}
