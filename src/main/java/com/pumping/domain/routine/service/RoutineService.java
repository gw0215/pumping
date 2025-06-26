package com.pumping.domain.routine.service;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.dto.*;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routineexercise.dto.ExerciseSetResponse;
import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;
import com.pumping.domain.routineexercise.model.ExerciseSet;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;

    private final ExerciseRepository exerciseRepository;

    @Transactional
    public void create(Member member, RoutineExerciseRequests routineExerciseRequests) {
        Routine routine = new Routine(member, routineExerciseRequests.getRoutineName());

        routineExerciseRequests.getRoutineExerciseRequests().forEach(routineExerciseRequest -> {
            Exercise exercise = exerciseRepository.findById(routineExerciseRequest.getExerciseId())
                    .orElseThrow(() -> new EntityNotFoundException("운동을 찾을 수 없습니다: 운동 ID : " + routineExerciseRequest.getExerciseId()));

            RoutineExercise routineExercise = new RoutineExercise(routine, exercise, routineExerciseRequest.getOrder());

            routineExerciseRequest.getExerciseSetRequests().stream()
                    .map(req -> new ExerciseSet(routineExercise, req.getWeight(), req.getRepetition(), req.getSetCount()))
                    .forEach(routineExercise::addExerciseSet);

            routine.addRoutineExercise(routineExercise);
        });

        routineRepository.save(routine);
    }

    @Transactional
    public RoutineDetailResponse findById(Long id) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("루틴을 찾을 수 없습니다. 루틴 ID : " + id));

        List<RoutineExerciseResponse> routineExerciseResponses = routine.getRoutineExercises().stream()
                .map(routineExercise -> {
                    List<ExerciseSetResponse> exerciseSetResponses = routineExercise.getExerciseSets().stream()
                            .map(set -> new ExerciseSetResponse(set.getSetCount(), set.getWeight(), set.getRepetition()))
                            .toList();

                    return new RoutineExerciseResponse(
                            routineExercise.getExercise().getId(),
                            routineExercise.getExercise().getName(),
                            exerciseSetResponses
                    );
                })
                .toList();

        return new RoutineDetailResponse(routine.getId(), 0L, routine.getName(), routineExerciseResponses);
    }

    @Transactional
    public List<RoutineResponse> findAll(Long memberId) {
        return routineRepository.findAllByMemberId(memberId).stream()
                .map(routine -> new RoutineResponse(routine.getId(), routine.getName()))
                .toList();
    }

}
