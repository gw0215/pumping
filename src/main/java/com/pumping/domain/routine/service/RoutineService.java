package com.pumping.domain.routine.service;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.dto.*;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routineexercise.dto.ExerciseSetRequest;
import com.pumping.domain.routineexercise.dto.ExerciseSetResponse;
import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;
import com.pumping.domain.routineexercise.model.ExerciseSet;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;

    private final ExerciseRepository exerciseRepository;

    @Transactional
    public void create(Member member, RoutineExerciseRequests routineExerciseRequests) {
        Routine routine = new Routine(member, routineExerciseRequests.getRoutineName());

        List<RoutineExerciseRequest> requests = routineExerciseRequests.getRoutineExerciseRequests();

        for (RoutineExerciseRequest routineExerciseRequest : requests) {
            Exercise exercise = exerciseRepository.findById(routineExerciseRequest.getExerciseId()).orElseThrow(() -> new EntityNotFoundException("운동을 찾을 수 없습니다: 운동 ID : " + routineExerciseRequest.getExerciseId()));
            RoutineExercise routineExercise = new RoutineExercise(routine, exercise, routineExerciseRequest.getOrder());

            List<ExerciseSetRequest> exerciseSetRequests = routineExerciseRequest.getExerciseSetRequests();

            for (ExerciseSetRequest exerciseSetRequest : exerciseSetRequests) {
                routineExercise.addExerciseSet(new ExerciseSet(routineExercise, exerciseSetRequest.getWeight(), exerciseSetRequest.getRepetition(), exerciseSetRequest.getSetCount()));
            }

            routine.addRoutineExercise(routineExercise);
        }

        routineRepository.save(routine);
    }

    @Transactional
    public RoutineDetailResponse findById(Long id) {
        Routine routine = routineRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("루틴을 찾을 수 없습니다. 루틴 ID : " + id));

        List<RoutineExercise> routineExercises = routine.getRoutineExercises();
        List<RoutineExerciseResponse> routineExerciseResponses = new ArrayList<>();

        for (RoutineExercise routineExercise : routineExercises) {
            List<ExerciseSet> exerciseSets = routineExercise.getExerciseSets();

            List<ExerciseSetResponse> exerciseSetResponses = new ArrayList<>();

            for (ExerciseSet exerciseSet : exerciseSets) {
                exerciseSetResponses.add(new ExerciseSetResponse(exerciseSet.getSetCount(), exerciseSet.getWeight(), exerciseSet.getRepetition()));
            }

            routineExerciseResponses.add(new RoutineExerciseResponse(routineExercise.getExercise().getId(), routineExercise.getExercise().getName(), exerciseSetResponses));
        }

        return new RoutineDetailResponse(routine.getId(), 0L, routine.getName(), routineExerciseResponses);


    }

    @Transactional
    public List<RoutineResponse> findAll(Long memberId) {

        List<Routine> routines = routineRepository.findAllByMemberId(memberId);

        List<RoutineResponse> routineResponses = new ArrayList<>();

        for (Routine routine : routines) {
            routineResponses.add(new RoutineResponse(routine.getId(), routine.getName()));
        }

        return routineResponses;

    }

}
