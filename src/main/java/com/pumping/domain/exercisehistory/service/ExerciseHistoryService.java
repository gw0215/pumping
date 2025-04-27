package com.pumping.domain.exercisehistory.service;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.performedexerciseset.model.PerformedExerciseSet;
import com.pumping.domain.performedexerciseset.dto.PerformedExerciseSetRequest;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryResponse;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryUpdateRequest;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryWeekStatusResponse;
import com.pumping.domain.exercisehistory.model.ExerciseHistoryStatus;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import com.pumping.domain.exercisehistory.repository.ExerciseHistoryRepository;
import com.pumping.domain.routineexercise.dto.ExerciseSetResponse;
import com.pumping.domain.routineexercise.dto.RoutineExerciseResponse;
import com.pumping.domain.routineexercise.model.ExerciseSet;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExerciseHistoryService {

    private final ExerciseHistoryRepository exerciseHistoryRepository;

    private final RoutineRepository routineRepository;

    private final ExerciseRepository exerciseRepository;

    @Transactional
    public void save(Member member,Long routineId, LocalDate date) {

        Routine routine = routineRepository.findById(routineId).orElseThrow(RuntimeException::new);

        List<RoutineExercise> routineExercises = routine.getRoutineExercises();

        List<ExerciseHistory> exerciseHistories = new ArrayList<>();

        for (RoutineExercise routineExercise : routineExercises) {
            List<ExerciseSet> exerciseSets = routineExercise.getExerciseSets();

            ExerciseHistory exerciseHistory = new ExerciseHistory(member, routine, LocalTime.MIDNIGHT, ExerciseHistoryStatus.NOT_STARTED, date);
            Exercise exercise = routineExercise.getExercise();
            for (ExerciseSet exerciseSet : exerciseSets) {
                PerformedExerciseSet performedExerciseSet = new PerformedExerciseSet(exerciseHistory, exercise, exerciseSet.getWeight(), exerciseSet.getRepetition(), exerciseSet.getSetCount(), false);
                exerciseHistory.addPerformedExerciseSet(performedExerciseSet);
            }

            exerciseHistories.add(exerciseHistory);
        }

        exerciseHistoryRepository.saveAll(exerciseHistories);

    }

    @Transactional
    public void update(Long performedRoutineId, ExerciseHistoryUpdateRequest exerciseHistoryUpdateRequest) {
        ExerciseHistory exerciseHistory = exerciseHistoryRepository.findById(performedRoutineId).orElseThrow(() -> new EntityNotFoundException("루틴 수행 기록을 찾을 수 없습니다. 기록 ID : " + performedRoutineId));

        exerciseHistory.updatePerformedRoutineStatus(ExerciseHistoryStatus.COMPLETED);
        exerciseHistory.clearPerformedExerciseSet();
        List<PerformedExerciseSetRequest> performedExerciseSetRequests = exerciseHistoryUpdateRequest.getPerformedExerciseSetRequests();

        for (PerformedExerciseSetRequest performedExerciseSetRequest : performedExerciseSetRequests) {
            Long exerciseId = performedExerciseSetRequest.getExerciseId();
            Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() -> new EntityNotFoundException("운동을 찾을 수 없습니다. 운동 ID : " + exerciseId));
            PerformedExerciseSet performedExerciseSet = new PerformedExerciseSet(exerciseHistory, exercise, performedExerciseSetRequest.getWeight(), performedExerciseSetRequest.getRepetition(), performedExerciseSetRequest.getSetCount(), performedExerciseSetRequest.getChecked());
            exerciseHistory.addPerformedExerciseSet(performedExerciseSet);
        }

    }

    @Transactional
    public ExerciseHistoryWeekStatusResponse weekStatus(Member member) {

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        List<ExerciseHistory> exerciseHistoryList = exerciseHistoryRepository.findByMemberAndPerformedDateBetween(member, monday, sunday);

        long totalSeconds = 0;
        float totalVolume = 0;

        List<Long> totalSecondsPerDay = new ArrayList<>();
        List<Float> totalVolumePerDay = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            totalSecondsPerDay.add(0L);
            totalVolumePerDay.add(0f);
        }

        for (ExerciseHistory exerciseHistory : exerciseHistoryList) {
            LocalDate performedDate = exerciseHistory.getPerformedDate();
            int dayOfWeek = performedDate.getDayOfWeek().getValue() - 1;

            long routineSeconds = exerciseHistory.getPerformedTime().toSecondOfDay();
            float routineVolume = 0f;

            for (PerformedExerciseSet performedExerciseSet : exerciseHistory.getPerformedExerciseSets()) {
                routineVolume += performedExerciseSet.getWeight();
            }

            totalSeconds += routineSeconds;
            totalVolume += routineVolume;

            totalSecondsPerDay.set(dayOfWeek, totalSecondsPerDay.get(dayOfWeek) + routineSeconds);
            totalVolumePerDay.set(dayOfWeek, totalVolumePerDay.get(dayOfWeek) + routineVolume);
        }

        return new ExerciseHistoryWeekStatusResponse(totalSeconds, totalVolume, totalSecondsPerDay, totalVolumePerDay);
    }


    @Transactional
    public void endExerciseHistory(Long performedRoutineId, LocalTime performedTime) {
        ExerciseHistory exerciseHistory = exerciseHistoryRepository.findById(performedRoutineId).orElseThrow(() -> new EntityNotFoundException("루틴 수행 기록을 찾을 수 없습니다. 기록 ID : " + performedRoutineId));

        exerciseHistory.updatePerformedTime(performedTime);
        exerciseHistory.updatePerformedRoutineStatus(ExerciseHistoryStatus.COMPLETED);

    }

    @Transactional
    public ExerciseHistoryResponse findById(Long performedRoutineId, Long routineId) {
        ExerciseHistory exerciseHistory = exerciseHistoryRepository.findById(performedRoutineId).orElseThrow(() -> new EntityNotFoundException("루틴 수행 기록을 찾을 수 없습니다. 기록 ID : " + performedRoutineId));

        Routine routine = routineRepository.findById(routineId).orElseThrow(() -> new EntityNotFoundException("루틴을 찾을 수 없습니다. 루틴 ID : " + routineId));

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

        return new ExerciseHistoryResponse(routine.getId(), exerciseHistory.getId(), exerciseHistory.getExerciseHistoryStatus().toString(), routine.getName(), routineExerciseResponses);

    }

    @Transactional
    public Optional<ExerciseHistoryResponse> findByMemberIdAndPerformedDate(Long memberId, LocalDate date) {
        Optional<ExerciseHistory> optionalRoutineDate =  exerciseHistoryRepository.findByMemberIdAndPerformedDate(memberId, date);

        if (optionalRoutineDate.isEmpty()) {
            return Optional.empty();
        }

        ExerciseHistory exerciseHistory = optionalRoutineDate.get();

        Routine routine = exerciseHistory.getRoutine();

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

        return Optional.of(new ExerciseHistoryResponse(routine.getId(), exerciseHistory.getId(), exerciseHistory.getExerciseHistoryStatus().toString(), routine.getName(), routineExerciseResponses));

    }

    @Transactional
    public void delete(Long routineDateId) {
       exerciseHistoryRepository.deleteById(routineDateId);
    }

}
