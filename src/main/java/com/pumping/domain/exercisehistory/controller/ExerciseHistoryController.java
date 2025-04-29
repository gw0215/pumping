package com.pumping.domain.exercisehistory.controller;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryResponse;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryRequest;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryUpdateRequest;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryWeekStatusResponse;
import com.pumping.domain.exercisehistory.service.ExerciseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequiredArgsConstructor
public class ExerciseHistoryController {

    private final ExerciseHistoryService exerciseHistoryService;

    @PostMapping("/exercise-history")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @SessionAttribute("member") Member member,
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

    @GetMapping("/exercise-history/{exerciseHistoryId}")
    public ResponseEntity<ExerciseHistoryResponse> findById(
            @SessionAttribute("member") Member member,
            @PathVariable("exerciseHistoryId") Long exerciseHistoryId
    ) {
        ExerciseHistoryResponse exerciseHistoryResponse = exerciseHistoryService.findById(exerciseHistoryId);
        return ResponseEntity.ok(exerciseHistoryResponse);
    }

    @GetMapping("/exercise-history")
    public ResponseEntity<ExerciseHistoryResponse> findByMemberIdAndPerformedDate(
            @SessionAttribute("member") Member member,
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
            @SessionAttribute("member") Member member
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
}
