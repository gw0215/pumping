package com.pumping.domain.routinedate.service;

import com.pumping.domain.routine.dto.RoutineDetailResponse;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routinedate.model.RoutineDate;
import com.pumping.domain.routinedate.repository.RoutineDateRepository;
import com.pumping.domain.routineexercise.dto.ExerciseSetDetail;
import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public RoutineDetailResponse findByMemberIdAndPerformedDate(Long memberId, LocalDate date) {

        Routine routine = routineRepository
                .findByMemberIdAndPerformedDate(memberId, date)
                .orElse(null);

        if (routine == null) {
            return null;
        }

        List<RoutineExercise> routineExercises = routine.getRoutineExercises();

        Map<Integer, List<RoutineExercise>> collect = routineExercises.stream().collect(Collectors.groupingBy(RoutineExercise::getSetOrder));

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

}
