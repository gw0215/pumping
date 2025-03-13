package com.pumping.domain.routine.service;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.dto.RoutineDetailResponse;
import com.pumping.domain.routine.dto.RoutineExerciseRequests;
import com.pumping.domain.routine.dto.RoutineResponse;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routineexercise.dto.ExerciseSetDetail;
import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

            routine.getRoutineExercises().add(new RoutineExercise(routine, exercise, r.getWeight(), r.getCount(), r.getSetCount(), r.getOrder()));
        });

        routineRepository.save(routine);
    }

    @Transactional
    public RoutineDetailResponse findById(Long id) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(RuntimeException::new);

        List<RoutineExercise> routineExercises = routine.getRoutineExercises();

        Map<Integer, List<RoutineExercise>> collect = routineExercises.stream().collect(Collectors.groupingBy(RoutineExercise::getOrder));

        List<RoutineExerciseResponse> exerciseResponses = collect.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<RoutineExercise> exercises = entry.getValue();
                    RoutineExercise first = exercises.get(0);

                    List<ExerciseSetDetail> setDetails = exercises.stream()
                            .map(re -> new ExerciseSetDetail(re.getSetCount(), re.getWeight(), re.getCount()))
                            .sorted(Comparator.comparing(ExerciseSetDetail::getSet))
                            .collect(Collectors.toList());

                    return new RoutineExerciseResponse(first.getExercise().getName(), setDetails);
                })
                .collect(Collectors.toList());
        
        return new RoutineDetailResponse(routine.getId(), routine.getName(), exerciseResponses);


    }

    @Transactional
    public List<RoutineResponse> findAll(Long memberId) {

        List<Routine> routines = routineRepository.findAllByMemberId(memberId);

        return routines.stream()
                .map(routine ->
                        new RoutineResponse(routine.getId(), routine.getName()))
                .collect(Collectors.toList());

    }


}
