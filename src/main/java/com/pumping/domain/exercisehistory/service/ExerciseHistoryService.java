package com.pumping.domain.exercisehistory.service;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryResponse;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryUpdateRequest;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryWeekStatusResponse;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import com.pumping.domain.exercisehistory.model.ExerciseHistoryStatus;
import com.pumping.domain.exercisehistory.repository.ExerciseHistoryRepository;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.performedexercise.dto.PerformedExerciseRequest;
import com.pumping.domain.performedexercise.dto.PerformedExerciseResponse;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetRequest;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetResponse;
import com.pumping.domain.performedexercise.model.PerformedExercise;
import com.pumping.domain.performedexercise.model.PerformedExerciseSet;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import com.pumping.domain.routineexercise.model.ExerciseSet;
import com.pumping.domain.routineexercise.model.RoutineExercise;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExerciseHistoryService {

    private final ExerciseHistoryRepository exerciseHistoryRepository;

    private final RoutineRepository routineRepository;

    private final ExerciseRepository exerciseRepository;

    @Transactional
    public void save(Member member, Long routineId, LocalDate date) {

        Routine routine = routineRepository.findById(routineId).orElseThrow(RuntimeException::new);

        List<RoutineExercise> routineExercises = routine.getRoutineExercises();

        ExerciseHistory exerciseHistory = new ExerciseHistory(member, routine, LocalTime.MIDNIGHT, ExerciseHistoryStatus.NOT_STARTED, date);
        for (RoutineExercise routineExercise : routineExercises) {
            List<ExerciseSet> exerciseSets = routineExercise.getExerciseSets();

            Exercise exercise = routineExercise.getExercise();
            PerformedExercise performedExercise = new PerformedExercise(exerciseHistory, exercise, routineExercise.getExerciseOrder());

            for (ExerciseSet exerciseSet : exerciseSets) {
                performedExercise.addPerformedExerciseSet(new PerformedExerciseSet(performedExercise, exerciseSet.getWeight(), exerciseSet.getRepetition(), exerciseSet.getSetCount(), false));
            }

            exerciseHistory.addPerformedExercise(performedExercise);
        }

        exerciseHistoryRepository.save(exerciseHistory);

    }

    @Transactional
    public void update(Long exerciseHistoryId, ExerciseHistoryUpdateRequest request) {
        ExerciseHistory exerciseHistory = exerciseHistoryRepository.findById(exerciseHistoryId)
                .orElseThrow(() -> new EntityNotFoundException("루틴 수행 기록을 찾을 수 없습니다. ID: " + exerciseHistoryId));

        List<PerformedExercise> performedExercises = exerciseHistory.getPerformedExercises();

        List<Long> deletedSetIds = request.getDeletedSetIds();
        for (PerformedExercise performedExercise : performedExercises) {
            List<PerformedExerciseSet> sets = performedExercise.getPerformedExerciseSets();
            for (int i = sets.size() - 1; i >= 0; i--) {
                if (deletedSetIds.contains(sets.get(i).getId())) {
                    sets.remove(i);
                }
            }
        }

        List<PerformedExerciseSetRequest> addedSets = request.getAddedSets();
        for (PerformedExerciseSetRequest addRequest : addedSets) {
            Long performedExerciseId = addRequest.getPerformedExerciseId();
            for (PerformedExercise performedExercise : performedExercises) {
                if (performedExercise.getId().equals(performedExerciseId)) {
                    PerformedExerciseSet performedExerciseSet = new PerformedExerciseSet(
                            performedExercise,
                            addRequest.getWeight(),
                            addRequest.getRepetition(),
                            addRequest.getSetCount(),
                            addRequest.getCompleted()
                    );
                    performedExercise.addPerformedExerciseSet(performedExerciseSet);
                    break;
                }
            }
        }

        List<PerformedExerciseSetRequest> updatedSets = request.getUpdatedSets();
        for (PerformedExerciseSetRequest updateRequest : updatedSets) {
            Long updateRequestPerformedExerciseSetId = updateRequest.getPerformedExerciseSetId();
            for (PerformedExercise performedExercise : performedExercises) {
                for (PerformedExerciseSet performedExerciseSet : performedExercise.getPerformedExerciseSets()) {
                    if (performedExerciseSet.getId().equals(updateRequestPerformedExerciseSetId)) {
                        performedExerciseSet.updateWeight(updateRequest.getWeight());
                        performedExerciseSet.updateRepetition(updateRequest.getRepetition());
                        performedExerciseSet.updateSetCount(updateRequest.getSetCount());
                        performedExerciseSet.updateCompleted(updateRequest.getCompleted());
                        break;
                    }
                }
            }
        }
        List<PerformedExerciseRequest> newExercises = request.getNewExercises();
        if (newExercises != null) {
            for (PerformedExerciseRequest newExerciseReq : newExercises) {
                Exercise exercise = exerciseRepository.findById(newExerciseReq.getExerciseId())
                        .orElseThrow(() -> new RuntimeException("운동 정보 없음"));

                PerformedExercise newExercise = new PerformedExercise(
                        exerciseHistory,
                        exercise,
                        newExerciseReq.getExerciseOrder()
                );

                for (PerformedExerciseSetRequest setReq : newExerciseReq.getPerformedExerciseSetRequests()) {
                    PerformedExerciseSet newSet = new PerformedExerciseSet(
                            newExercise,
                            setReq.getWeight(),
                            setReq.getRepetition(),
                            setReq.getSetCount(),
                            setReq.getCompleted()
                    );
                    newExercise.addPerformedExerciseSet(newSet);
                }

                exerciseHistory.addPerformedExercise(newExercise);
            }
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

            if (exerciseHistory.getExerciseHistoryStatus().equals(ExerciseHistoryStatus.COMPLETED)) {

                int dayOfWeek = performedDate.getDayOfWeek().getValue() - 1;


                long routineSeconds = exerciseHistory.getPerformedTime().toSecondOfDay();
                float routineVolume = 0f;

                for (PerformedExercise performedExercise : exerciseHistory.getPerformedExercises()) {
                    List<PerformedExerciseSet> performedExerciseSets = performedExercise.getPerformedExerciseSets();

                    for (PerformedExerciseSet performedExerciseSet : performedExerciseSets) {
                        if (performedExerciseSet.getCompleted()) {
                            routineVolume = performedExerciseSet.getWeight();
                        }
                    }

                }

                totalSeconds += routineSeconds;
                totalVolume += routineVolume;

                totalSecondsPerDay.set(dayOfWeek, totalSecondsPerDay.get(dayOfWeek) + routineSeconds);
                totalVolumePerDay.set(dayOfWeek, totalVolumePerDay.get(dayOfWeek) + routineVolume);
            }
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
    public Optional<ExerciseHistoryResponse> findByMemberIdAndPerformedDate(Long memberId, LocalDate date) {
        Optional<ExerciseHistory> optionalRoutineDate = exerciseHistoryRepository.findByMemberIdAndPerformedDate(memberId, date);

        if (optionalRoutineDate.isEmpty()) {
            return Optional.empty();
        }

        ExerciseHistory exerciseHistory = optionalRoutineDate.get();
        Routine routine = exerciseHistory.getRoutine();

        List<PerformedExercise> performedExercises = exerciseHistory.getPerformedExercises();

        List<PerformedExerciseResponse> performedExerciseResponses = new ArrayList<>();

        for (PerformedExercise performedExercise : performedExercises) {
            List<PerformedExerciseSet> performedExerciseSets = performedExercise.getPerformedExerciseSets();

            List<PerformedExerciseSetResponse> performedExerciseSetResponses = new ArrayList<>();
            for (PerformedExerciseSet performedExerciseSet : performedExerciseSets) {
                performedExerciseSetResponses.add(new PerformedExerciseSetResponse(performedExerciseSet.getId(), performedExerciseSet.getSetCount(), performedExerciseSet.getWeight(), performedExerciseSet.getRepetition(), performedExerciseSet.getCompleted()));
            }

            performedExerciseResponses.add(new PerformedExerciseResponse(performedExercise.getId(), performedExercise.getExercise().getId(), performedExercise.getExercise().getName(), performedExerciseSetResponses));
        }


        return Optional.of(new ExerciseHistoryResponse(routine.getId(), exerciseHistory.getId(), exerciseHistory.getExerciseHistoryStatus().toString(), routine.getName(), performedExerciseResponses));
    }

    @Transactional
    public void delete(Long routineDateId) {
        exerciseHistoryRepository.deleteById(routineDateId);
    }

}
