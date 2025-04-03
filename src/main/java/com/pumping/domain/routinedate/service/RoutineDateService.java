package com.pumping.domain.routinedate.service;

import com.pumping.domain.routine.dto.RoutineDetailResponse;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routinedate.model.RoutineDate;
import com.pumping.domain.routinedate.repository.RoutineDateRepository;
import com.pumping.domain.routineexercise.dto.ExerciseSetResponse;
import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;
import com.pumping.domain.routineexercise.model.ExerciseSet;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoutineDateService {

    private final RoutineDateRepository routineDateRepository;

    private final RoutineRepository routineRepository;

    @Transactional
    public void save(Long routineId, LocalDate date) {

        Routine routine = routineRepository.findById(routineId).orElseThrow(RuntimeException::new);

        RoutineDate routineDate = new RoutineDate(routine, date);

        routineDateRepository.save(routineDate);

    }

    @Transactional
    public Optional<RoutineDetailResponse> findByMemberIdAndPerformedDate(Long memberId, LocalDate date) {
        Optional<Routine> optionalRoutine =  routineRepository.findByMemberIdAndPerformedDate(memberId, date);

        if (optionalRoutine.isEmpty()) {
            return Optional.empty();
        }

        Routine routine = optionalRoutine.get();

        List<RoutineExercise> routineExercises = routine.getRoutineExercises();
        List<RoutineExerciseResponse> routineExerciseResponses = new ArrayList<>();

        for (RoutineExercise routineExercise : routineExercises) {
            List<ExerciseSet> exerciseSets = routineExercise.getExerciseSets();

            List<ExerciseSetResponse> exerciseSetResponses = new ArrayList<>();

            for (ExerciseSet exerciseSet : exerciseSets) {
                exerciseSetResponses.add(new ExerciseSetResponse(exerciseSet.getSetCount(), exerciseSet.getWeight(), exerciseSet.getCount()));
            }

            routineExerciseResponses.add(new RoutineExerciseResponse(routineExercise.getExercise().getName(), exerciseSetResponses));
        }

        return Optional.of(new RoutineDetailResponse(routine.getId(), routine.getName(), routineExerciseResponses));

    }

}
