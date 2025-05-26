package com.pumping.domain.exercisehistory.dto;

import com.pumping.domain.exercisehistory.repository.TopExerciseDto;

import java.util.List;

public record TopExerciseResponse(List<TopExerciseDto> chests, List<TopExerciseDto> backs,
                                  List<TopExerciseDto> shoulders, List<TopExerciseDto> arms, List<TopExerciseDto> cores,
                                  List<TopExerciseDto> legs, List<TopExerciseDto> hips) {

}
