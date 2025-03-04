package com.pumping.domain.routine.service;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.dto.RoutineExerciseRequests;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

            routine.getRoutineExercises().add(new RoutineExercise(routine, exercise, r.getWeight(), r.getCount(), r.getSetCount()));
        });

        routineRepository.save(routine);
    }


}
