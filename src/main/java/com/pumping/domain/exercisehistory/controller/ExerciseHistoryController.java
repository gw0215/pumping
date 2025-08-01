package com.pumping.domain.exercisehistory.controller;

import com.pumping.domain.exercisehistory.dto.*;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.exercisehistory.service.ExerciseHistoryService;
import com.pumping.global.common.annotation.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ExerciseHistoryController {

    private final ExerciseHistoryService exerciseHistoryService;

    @PostMapping("/exercise-history")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @AuthMember Member member,
            @RequestBody ExerciseHistoryRequest exerciseHistoryRequest
    ) {
        exerciseHistoryService.save(member, exerciseHistoryRequest.getRoutineId(), exerciseHistoryRequest.getPerformedDate());
    }

    @PatchMapping("/exercise-history/{exerciseHistoryId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void update(
            @RequestBody ExerciseHistoryUpdateRequest exerciseHistoryUpdateRequest,
            @PathVariable("exerciseHistoryId") Long exerciseHistoryId
    ) {
        exerciseHistoryService.update(exerciseHistoryId, exerciseHistoryUpdateRequest);
    }

    @GetMapping("/exercise-history")
    public ResponseEntity<ExerciseHistoryResponse> findByMemberIdAndPerformedDate(
            @AuthMember Member member,
            @RequestParam("performedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate performedDate
    ) {
        return exerciseHistoryService.findByMemberIdAndPerformedDate(member.getId(), performedDate)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/exercise-history/{exerciseHistoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("exerciseHistoryId") Long exerciseHistoryId
    ) {
        exerciseHistoryService.delete(exerciseHistoryId);
    }

    @GetMapping("/exercise-history/weekstatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ExerciseHistoryWeekStatusResponse> weekStatus(
            @AuthMember Member member
    ) {
        ExerciseHistoryWeekStatusResponse exerciseHistoryWeekStatusResponse = exerciseHistoryService.weekStatus(member);
        return ResponseEntity.ok(exerciseHistoryWeekStatusResponse);
    }

    @PatchMapping("/exercise-history/{exerciseHistoryId}/end")
    @ResponseStatus(HttpStatus.OK)
    public void endExerciseHistory(
            @PathVariable("exerciseHistoryId") Long exerciseHistoryId,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        exerciseHistoryService.endExerciseHistory(exerciseHistoryId, endTime);
    }

    @PatchMapping("/performed-exercise-set/{performedExerciseSetId}")
    @ResponseStatus(HttpStatus.OK)
    public void checkSet(
            @PathVariable("performedExerciseSetId") Long performedExerciseSetId) {
        exerciseHistoryService.checkSet(performedExerciseSetId);
    }

    @GetMapping("/exercise-history/exercise-part-analyze")
    public ResponseEntity< List<ExercisePartSetCountDto>> getWeeklyExerciseSetCount(
            @AuthMember Member member
    ) {
        List<ExercisePartSetCountDto> weeklySetCountByExercisePart = exerciseHistoryService.getWeeklySetCountByExercisePart(member.getId());
        return ResponseEntity.ok(weeklySetCountByExercisePart);
    }

    @GetMapping("/exercise-history/last-month-compare")
    public ResponseEntity<List<PartVolumeComparisonDto>> compareMonthlyVolume(
            @AuthMember Member member) {
        List<PartVolumeComparisonDto> result = exerciseHistoryService.compareThisMonthAndLastMonthVolume(member.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/exercise-history/top5")
    public ResponseEntity<TopExerciseResponse> getTop5ExercisesByPart(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        TopExerciseResponse top5ExercisesByPart = exerciseHistoryService.getTop5ExercisesByPart(startDate, endDate);
        return ResponseEntity.ok(top5ExercisesByPart);
    }
}
