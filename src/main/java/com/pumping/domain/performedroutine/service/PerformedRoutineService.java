package com.pumping.domain.performedroutine.service;

import com.pumping.domain.performedexerciseset.model.PerformedExerciseSet;
import com.pumping.domain.performedroutine.dto.PerformedRoutineResponse;
import com.pumping.domain.performedroutine.model.PerformedRoutineStatus;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.performedroutine.model.PerformedRoutine;
import com.pumping.domain.performedroutine.repository.PerformedRoutineRepository;
import com.pumping.domain.routineexercise.dto.ExerciseSetResponse;
import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;
import com.pumping.domain.routineexercise.model.ExerciseSet;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PerformedRoutineService {

    private final PerformedRoutineRepository performedRoutineRepository;

    private final RoutineRepository routineRepository;

    @Transactional
    public void save(Long routineId, LocalDate date) {

        Routine routine = routineRepository.findById(routineId).orElseThrow(RuntimeException::new);

        List<RoutineExercise> routineExercises = routine.getRoutineExercises();

        List<PerformedRoutine> performedRoutines = new ArrayList<>();

        for (RoutineExercise routineExercise : routineExercises) {
            List<ExerciseSet> exerciseSets = routineExercise.getExerciseSets();

            PerformedRoutine performedRoutine = new PerformedRoutine(routine, LocalTime.MIDNIGHT, PerformedRoutineStatus.NOT_STARTED, date);

            for (ExerciseSet exerciseSet : exerciseSets) {
                PerformedExerciseSet performedExerciseSet = new PerformedExerciseSet(performedRoutine, exerciseSet.getWeight(), exerciseSet.getRepetition(), exerciseSet.getSetCount(), false);
                performedRoutine.addPerformedExerciseSet(performedExerciseSet);
            }

            performedRoutines.add(performedRoutine);
        }

        performedRoutineRepository.saveAll(performedRoutines);

    }

    @Transactional
    public void startExercise(Long performedRoutineId) {
        PerformedRoutine performedRoutine = performedRoutineRepository.findById(performedRoutineId).orElseThrow(() -> new EntityNotFoundException("루틴 수행 기록을 찾을 수 없습니다. 기록 ID : " + performedRoutineId));

        performedRoutine.updatePerformedRoutineStatus(PerformedRoutineStatus.IN_PROGRESS);

    }


    @Transactional
    public void endExercise(Long performedRoutineId,LocalTime performedTime) {
        PerformedRoutine performedRoutine = performedRoutineRepository.findById(performedRoutineId).orElseThrow(() -> new EntityNotFoundException("루틴 수행 기록을 찾을 수 없습니다. 기록 ID : " + performedRoutineId));

        performedRoutine.updatePerformedTime(performedTime);
        performedRoutine.updatePerformedRoutineStatus(PerformedRoutineStatus.COMPLETED);

    }

    @Transactional
    public PerformedRoutineResponse findById(Long performedRoutineId) {
        PerformedRoutine performedRoutine = performedRoutineRepository.findById(performedRoutineId).orElseThrow(() -> new EntityNotFoundException("루틴 수행 기록을 찾을 수 없습니다. 기록 ID : " + performedRoutineId));
        Routine routine = performedRoutine.getRoutine();

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

        return new PerformedRoutineResponse(routine.getId(), performedRoutine.getId(), performedRoutine.getPerformedRoutineStatus().toString(), routine.getName(), routineExerciseResponses);

    }

    @Transactional
    public Optional<PerformedRoutineResponse> findByMemberIdAndPerformedDate(Long memberId, LocalDate date) {
        Optional<PerformedRoutine> optionalRoutineDate =  performedRoutineRepository.findByMemberIdAndPerformedDate(memberId, date);

        if (optionalRoutineDate.isEmpty()) {
            return Optional.empty();
        }

        PerformedRoutine performedRoutine = optionalRoutineDate.get();

        Routine routine = performedRoutine.getRoutine();

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

        return Optional.of(new PerformedRoutineResponse(routine.getId(), performedRoutine.getId(), performedRoutine.getPerformedRoutineStatus().toString(), routine.getName(), routineExerciseResponses));

    }

    @Transactional
    public void delete(Long routineDateId) {
       performedRoutineRepository.deleteById(routineDateId);
    }

}
