package com.pumping.domain.exercisehistory.service;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.model.ExercisePart;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.exercisehistory.dto.*;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import com.pumping.domain.exercisehistory.model.ExerciseHistoryStatus;
import com.pumping.domain.exercisehistory.repository.*;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.performedexercise.dto.PerformedExerciseRequest;
import com.pumping.domain.performedexercise.dto.PerformedExerciseResponse;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetRequest;
import com.pumping.domain.performedexercise.dto.PerformedExerciseSetResponse;
import com.pumping.domain.performedexercise.model.PerformedExercise;
import com.pumping.domain.performedexercise.model.PerformedExerciseSet;
import com.pumping.domain.performedexercise.repository.PerformedExerciseSetRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExerciseHistoryService {

    private final ExerciseHistoryRepository exerciseHistoryRepository;

    private final PerformedExerciseSetRepository performedExerciseSetRepository;

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

        List<WeeklyExerciseHistoryStatsDto> weeklyExerciseHistoryStatsDtos = exerciseHistoryRepository.findWeeklyStats(member.getId(), monday, sunday);

        Map<LocalDate, WeeklyExerciseHistoryStatsDto> statsByDate = new HashMap<>();
        for (WeeklyExerciseHistoryStatsDto weeklyExerciseHistoryStatsDto : weeklyExerciseHistoryStatsDtos) {
            statsByDate.put(weeklyExerciseHistoryStatsDto.getPerformedDate(), weeklyExerciseHistoryStatsDto);
        }

        List<Long> totalSecondsPerDay = new ArrayList<>();
        List<Float> totalVolumePerDay = new ArrayList<>();

        long totalSeconds = 0;
        float totalVolume = 0f;

        for (int i = 0; i < 7; i++) {
            LocalDate localDate = monday.plusDays(i);
            WeeklyExerciseHistoryStatsDto weeklyExerciseHistoryStatsDto = statsByDate.get(localDate);

            long seconds = 0L;
            float volume = 0f;

            if (weeklyExerciseHistoryStatsDto != null) {
                if (weeklyExerciseHistoryStatsDto.getTotalSeconds() != null) {
                    seconds = weeklyExerciseHistoryStatsDto.getTotalSeconds();
                }
                if (weeklyExerciseHistoryStatsDto.getTotalVolume() != null) {
                    volume = weeklyExerciseHistoryStatsDto.getTotalVolume();
                }
            }

            totalSecondsPerDay.add(seconds);
            totalVolumePerDay.add(volume);

            totalSeconds += seconds;
            totalVolume += volume;
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

    @Transactional
    public void checkSet(Long performedExerciseSetId) {
        PerformedExerciseSet performedExerciseSet = performedExerciseSetRepository.findById(performedExerciseSetId).orElseThrow(() -> new EntityNotFoundException("루틴 수행 기록 세트 정보를 찾을 수 없습니다. ID: " + performedExerciseSetId));

        performedExerciseSet.updateCompleted(!performedExerciseSet.getCompleted());
    }

    @Transactional(readOnly = true)
    public List<ExercisePartSetCountDto> getWeeklySetCountByExercisePart(Long memberId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        List<ExercisePartSetCountDto> exercisePartSetCountDtos = exerciseHistoryRepository.countSetPerExercisePart(memberId, startDate, endDate);

        List<ExercisePartSetCountDto> response = new ArrayList<>();

        for (ExercisePart exercisePart : ExercisePart.values()) {
            Long setCount = 0L;

            for (ExercisePartSetCountDto exercisePartSetCountDto : exercisePartSetCountDtos) {
                if (exercisePart.equals(exercisePartSetCountDto.getExercisePart())) {
                    setCount = exercisePartSetCountDto.getSetCount();
                    break;
                }
            }

            response.add(new ExercisePartSetCountDto(exercisePart, setCount));
        }

        return response;
    }

    @Transactional
    public List<PartVolumeComparisonDto> compareThisMonthAndLastMonthVolume(Long memberId) {
        LocalDate now = LocalDate.now();

        LocalDate thisMonthStart = now.withDayOfMonth(1);
        LocalDate thisMonthEnd = now.withDayOfMonth(now.lengthOfMonth());

        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

        List<MonthlyPartVolumeDto> thisMonthData = exerciseHistoryRepository.findMonthlyVolumeByPart(memberId, thisMonthStart, thisMonthEnd);
        List<MonthlyPartVolumeDto> lastMonthData = exerciseHistoryRepository.findMonthlyVolumeByPart(memberId, lastMonthStart, lastMonthEnd);

        List<PartVolumeComparisonDto> result = new ArrayList<>();

        for (ExercisePart part : ExercisePart.values()) {
            double thisMonthVolume = 0.0;
            double lastMonthVolume = 0.0;

            for (MonthlyPartVolumeDto monthlyPartVolumeDto : thisMonthData) {
                if (monthlyPartVolumeDto.getPart() == part) {
                    thisMonthVolume = monthlyPartVolumeDto.getTotalVolume();
                    break;
                }
            }

            for (MonthlyPartVolumeDto monthlyPartVolumeDto : lastMonthData) {
                if (monthlyPartVolumeDto.getPart() == part) {
                    lastMonthVolume = monthlyPartVolumeDto.getTotalVolume();
                    break;
                }
            }

            result.add(new PartVolumeComparisonDto(part, thisMonthVolume, lastMonthVolume));
        }

        return result;
    }


}
