package com.pumping.domain.exercisehistory.service;

import com.pumping.domain.exercise.model.ExercisePart;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.exercisehistory.dto.*;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import com.pumping.domain.exercisehistory.model.ExerciseHistoryStatus;
import com.pumping.domain.exercisehistory.repository.ExerciseHistoryRepository;
import com.pumping.domain.exercisehistory.repository.MonthlyPartVolumeDto;
import com.pumping.domain.exercisehistory.repository.WeeklyExerciseHistoryStatsDto;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.domain.performedexercise.dto.PerformedExerciseResponse;
import com.pumping.domain.performedexercise.model.PerformedExercise;
import com.pumping.domain.performedexercise.model.PerformedExerciseSet;
import com.pumping.domain.performedexercise.repository.PerformedExerciseSetRepository;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routine.repository.RoutineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseHistoryService {

    private final ExerciseHistoryRepository exerciseHistoryRepository;

    private final PerformedExerciseSetRepository performedExerciseSetRepository;

    private final RoutineRepository routineRepository;

    private final ExerciseRepository exerciseRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public void save(Member member, Long routineId, LocalDate date) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new EntityNotFoundException("루틴을 찾을 수 없습니다. ID: " + routineId));

        ExerciseHistory exerciseHistory = routine.createExerciseHistory(member, date);
        exerciseHistoryRepository.save(exerciseHistory);
    }

    @Transactional
    public void update(Long exerciseHistoryId, ExerciseHistoryUpdateRequest request) {
        ExerciseHistory history = exerciseHistoryRepository.findById(exerciseHistoryId)
                .orElseThrow(() -> new EntityNotFoundException("루틴 수행 기록을 찾을 수 없습니다. ID: " + exerciseHistoryId));

        history.updatePerformedSets(request.getUpdatedSets());
        history.deletePerformedSets(request.getDeletedSetIds());
        history.addPerformedSets(request.getAddedSets());
        history.addNewExercises(request.getNewExercises(), exerciseRepository);
    }

    @Transactional
    public ExerciseHistoryWeekStatusResponse weekStatus(Member member) {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        Map<LocalDate, WeeklyExerciseHistoryStatsDto> statsByDate = fetchStatsByDate(member, monday, sunday);
        DailyStatAggregate aggregate = computeWeeklyAggregate(statsByDate, monday);

        return new ExerciseHistoryWeekStatusResponse(
                aggregate.totalSeconds,
                aggregate.totalVolume,
                aggregate.totalSecondsPerDay,
                aggregate.totalVolumePerDay
        );
    }

    private DailyStatAggregate computeWeeklyAggregate(Map<LocalDate, WeeklyExerciseHistoryStatsDto> statsByDate, LocalDate startDate) {
        List<Long> totalSecondsPerDay = new ArrayList<>();
        List<Float> totalVolumePerDay = new ArrayList<>();

        long totalSeconds = 0;
        float totalVolume = 0f;

        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            WeeklyExerciseHistoryStatsDto dto = statsByDate.getOrDefault(date, null);

            long seconds = dto != null && dto.getTotalSeconds() != null ? dto.getTotalSeconds() : 0L;
            float volume = dto != null && dto.getTotalVolume() != null ? dto.getTotalVolume() : 0f;

            totalSecondsPerDay.add(seconds);
            totalVolumePerDay.add(volume);

            totalSeconds += seconds;
            totalVolume += volume;
        }

        return new DailyStatAggregate(totalSeconds, totalVolume, totalSecondsPerDay, totalVolumePerDay);
    }

    private Map<LocalDate, WeeklyExerciseHistoryStatsDto> fetchStatsByDate(Member member, LocalDate start, LocalDate end) {
        return exerciseHistoryRepository.findWeeklyStats(member.getId(), start, end)
                .stream()
                .collect(Collectors.toMap(
                        WeeklyExerciseHistoryStatsDto::getPerformedDate,
                        Function.identity()
                ));
    }

    @Transactional
    public void endExerciseHistory(Long performedRoutineId, LocalTime performedTime) {
        ExerciseHistory exerciseHistory = exerciseHistoryRepository.findById(performedRoutineId).orElseThrow(() -> new EntityNotFoundException("루틴 수행 기록을 찾을 수 없습니다. 기록 ID : " + performedRoutineId));

        exerciseHistory.updatePerformedTime(performedTime);
        exerciseHistory.updatePerformedRoutineStatus(ExerciseHistoryStatus.COMPLETED);

    }

    @Transactional
    public Optional<ExerciseHistoryResponse> findByMemberIdAndPerformedDate(Long memberId, LocalDate date) {
        return exerciseHistoryRepository.findByMemberIdAndPerformedDate(memberId, date)
                .map(ExerciseHistoryResponse::of);
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

        List<ExercisePartSetCountDto> rawResult = exerciseHistoryRepository
                .countSetPerExercisePart(memberId, startDate, endDate);

        Map<ExercisePart, Long> setCountMap = rawResult.stream()
                .collect(Collectors.toMap(
                        ExercisePartSetCountDto::getExercisePart,
                        ExercisePartSetCountDto::getSetCount
                ));

        return Arrays.stream(ExercisePart.values())
                .map(part -> new ExercisePartSetCountDto(part, setCountMap.getOrDefault(part, 0L)))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PartVolumeComparisonDto> compareThisMonthAndLastMonthVolume(Long memberId) {
        LocalDate now = LocalDate.now();

        LocalDate thisMonthStart = now.withDayOfMonth(1);
        LocalDate thisMonthEnd = now.withDayOfMonth(now.lengthOfMonth());

        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

        Map<ExercisePart, Double> thisMonthVolumeMap = exerciseHistoryRepository
                .findMonthlyVolumeByPart(memberId, thisMonthStart, thisMonthEnd).stream()
                .collect(Collectors.toMap(
                        MonthlyPartVolumeDto::getPart,
                        MonthlyPartVolumeDto::getTotalVolume
                ));

        Map<ExercisePart, Double> lastMonthVolumeMap = exerciseHistoryRepository
                .findMonthlyVolumeByPart(memberId, lastMonthStart, lastMonthEnd).stream()
                .collect(Collectors.toMap(
                        MonthlyPartVolumeDto::getPart,
                        MonthlyPartVolumeDto::getTotalVolume
                ));

        return Arrays.stream(ExercisePart.values())
                .map(part -> new PartVolumeComparisonDto(
                        part,
                        thisMonthVolumeMap.getOrDefault(part, 0.0),
                        lastMonthVolumeMap.getOrDefault(part, 0.0)
                ))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "topExercises", key = "#startDate + '-' + #endDate")
    @Transactional(readOnly = true)
    public TopExerciseResponse getTop5ExercisesByPart(LocalDate startDate, LocalDate endDate) {

        return new TopExerciseResponse(
                exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.CHEST.name()),
                exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.BACK.name()),
                exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.SHOULDERS.name()),
                exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.ARMS.name()),
                exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.CORE.name()),
                exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.LEGS.name()),
                exerciseHistoryRepository.findTop5ByPart(startDate, endDate, ExercisePart.HIP.name())
        );
    }

    @Async("reportExecutor")
    @Transactional
    public CompletableFuture<WeeklyReportDto> generateAllWeeklyReports(Member member, LocalDate startDate, LocalDate endDate) {
        List<ExerciseHistory> histories = fetchCompletedHistories(member, startDate, endDate);

        long totalWorkoutDays = calculateTotalWorkoutDays(histories);

        WeeklyReportDto.SummaryData summaryData = summarizeHistories(histories);

        String mostFrequentExercise = findMostFrequentExercise(summaryData.getExerciseSummaries());

        WeeklyReportDto reportDto = assembleWeeklyReportDto(
                member.getId(),
                totalWorkoutDays,
                summaryData,
                mostFrequentExercise
        );

        return CompletableFuture.completedFuture(reportDto);
    }

    private List<ExerciseHistory> fetchCompletedHistories(Member member, LocalDate start, LocalDate end) {
        return exerciseHistoryRepository.findByMemberAndPerformedDateBetweenAndExerciseHistoryStatus(
                member, start, end, ExerciseHistoryStatus.COMPLETED
        );
    }

    private long calculateTotalWorkoutDays(List<ExerciseHistory> histories) {
        return histories.stream()
                .map(ExerciseHistory::getPerformedDate)
                .distinct()
                .count();
    }

    private WeeklyReportDto.SummaryData summarizeHistories(List<ExerciseHistory> histories) {
        Map<String, WeeklyReportDto.ExerciseSummary> summaryMap = new HashMap<>();
        int totalSets = 0, totalReps = 0, totalMinutes = 0;
        double totalWeight = 0.0;

        for (ExerciseHistory history : histories) {
            if (history.getPerformedTime() != null) {
                totalMinutes += history.getPerformedTime().getHour() * 60 + history.getPerformedTime().getMinute();
            }

            for (PerformedExercise pe : history.getPerformedExercises()) {
                String name = pe.getExercise().getName();
                WeeklyReportDto.ExerciseSummary summary = summaryMap.getOrDefault(
                        name,
                        new WeeklyReportDto.ExerciseSummary(name, 0, 0, 0.0)
                );

                int sets = 0, reps = 0;
                double weight = 0.0;

                for (PerformedExerciseSet set : pe.getPerformedExerciseSets()) {
                    sets++;
                    int rep = Optional.ofNullable(set.getRepetition()).orElse(0);
                    double w = Optional.ofNullable(set.getWeight()).orElse(0.0f);
                    reps += rep;
                    weight += w * rep;
                }

                summaryMap.put(name, new WeeklyReportDto.ExerciseSummary(
                        name,
                        summary.getTotalSets() + sets,
                        summary.getTotalReps() + reps,
                        summary.getTotalWeight() + weight
                ));

                totalSets += sets;
                totalReps += reps;
                totalWeight += weight;
            }
        }

        return new WeeklyReportDto.SummaryData(
                totalSets,
                totalReps,
                totalWeight,
                totalMinutes,
                new ArrayList<>(summaryMap.values())
        );
    }

    private String findMostFrequentExercise(List<WeeklyReportDto.ExerciseSummary> summaries) {
        return summaries.stream()
                .max(Comparator.comparingInt(WeeklyReportDto.ExerciseSummary::getTotalSets))
                .map(WeeklyReportDto.ExerciseSummary::getExerciseName)
                .orElse("");
    }

    private WeeklyReportDto assembleWeeklyReportDto(Long memberId, long workoutDays, WeeklyReportDto.SummaryData data, String frequentExercise) {
        int averageDuration = workoutDays > 0 ? data.getTotalMinutes() / (int) workoutDays : 0;
        return new WeeklyReportDto(
                memberId,
                workoutDays,
                data.getTotalSets(),
                data.getTotalReps(),
                data.getTotalWeight(),
                averageDuration,
                frequentExercise,
                data.getExerciseSummaries()
        );
    }

    private static class DailyStatAggregate {
        long totalSeconds;
        float totalVolume;
        List<Long> totalSecondsPerDay;
        List<Float> totalVolumePerDay;

        public DailyStatAggregate(long totalSeconds, float totalVolume,
                                  List<Long> secondsPerDay, List<Float> volumePerDay) {
            this.totalSeconds = totalSeconds;
            this.totalVolume = totalVolume;
            this.totalSecondsPerDay = secondsPerDay;
            this.totalVolumePerDay = volumePerDay;
        }
    }


}
