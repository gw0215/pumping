package com.pumping.domain.exercise.service;

import com.pumping.domain.exercise.dto.ExerciseResponse;
import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.model.ExercisePart;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    @Transactional
    public List<ExerciseResponse> findAll(String part) {

        List<Exercise> exercises = exerciseRepository.findAllByExercisePart(ExercisePart.valueOf(part));

        List<ExerciseResponse> exerciseResponses = new ArrayList<>();

        for (Exercise exercise : exercises) {
            ExerciseResponse exerciseResponse = new ExerciseResponse(exercise.getId(), exercise.getName(), exercise.getExplanation(), exercise.getExercisePart().toString());
            exerciseResponses.add(exerciseResponse);
        }

        return exerciseResponses;


    }

}
