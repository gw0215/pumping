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
        Optional<RoutineDate> optionalRoutineDate =  routineDateRepository.findByMemberIdAndPerformedDate(memberId, date);

        if (optionalRoutineDate.isEmpty()) {
            return Optional.empty();
        }

        RoutineDate routineDate = optionalRoutineDate.get();

        Routine routine = routineDate.getRoutine();

        List<RoutineExercise> routineExercises = routine.getRoutineExercises();
        List<RoutineExerciseResponse> routineExerciseResponses = new ArrayList<>();

        for (RoutineExercise routineExercise : routineExercises) {
            List<ExerciseSet> exerciseSets = routineExercise.getExerciseSets();

            List<ExerciseSetResponse> exerciseSetResponses = new ArrayList<>();

            for (ExerciseSet exerciseSet : exerciseSets) {
                exerciseSetResponses.add(new ExerciseSetResponse(exerciseSet.getSetCount(), exerciseSet.getWeight(), exerciseSet.getRepetition()));
            }

            routineExerciseResponses.add(new RoutineExerciseResponse(routineExercise.getExercise().getName(), exerciseSetResponses));
        }

        return Optional.of(new RoutineDetailResponse(routine.getId(), routineDate.getId(), routine.getName(), routineExerciseResponses));

    }

    @Transactional
    public void delete(Long routineDateId) {
       routineDateRepository.deleteById(routineDateId);
    }

}
