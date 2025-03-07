package com.pumping.domain.routine.service;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.dto.RoutineExerciseRequests;
import com.pumping.domain.routine.dto.RoutineResponse;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;

    private final ExerciseRepository exerciseRepository;

    @Transactional
    public void create(Member member, RoutineExerciseRequests routineExerciseRequests) {
        Routine routine = new Routine(member, routineExerciseRequests.getRoutineName());

        routineExerciseRequests.getRoutineExerciseRequests().forEach(r -> {
            Exercise exercise = exerciseRepository.findById(r.getExerciseId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 운동을 찾을 수 없습니다: " + r.getExerciseId()));

            routine.getRoutineExercises().add(new RoutineExercise(routine, exercise, r.getWeight(), r.getCount()));
        });

        routineRepository.save(routine);
    }

    @Transactional
    public Page<RoutineResponse> findAll(Long memberId, Pageable pageable) {

        Page<Routine> routines = routineRepository.findAllByMemberId(memberId, pageable);

        return routines.map(routine ->
        {
            List<RoutineExerciseResponse> routineExerciseResponses = routine.getRoutineExercises().stream()
                    .map(routineExercise -> {
                        return new RoutineExerciseResponse(routineExercise.getExercise().getName(), routineExercise.getWeight(), routineExercise.getCount());
                    })
                    .collect(Collectors.toList());
            return new RoutineResponse(routine.getId(), routine.getName(), routineExerciseResponses);
        });


    }


}
