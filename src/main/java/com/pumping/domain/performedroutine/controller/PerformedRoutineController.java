package com.pumping.domain.performedroutine.controller;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.performedroutine.dto.PerformedRoutineResponse;
import com.pumping.domain.performedroutine.dto.PerformedRoutineRequest;
import com.pumping.domain.performedroutine.service.PerformedRoutineService;
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
public class PerformedRoutineController {

    private final PerformedRoutineService performedRoutineService;

    @PostMapping("/routine-date")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void createRoutineDate(
            @RequestBody PerformedRoutineRequest performedRoutineRequest
    ) {
        performedRoutineService.save(performedRoutineRequest.getRoutineId(), performedRoutineRequest.getPerformedDate());
    }

    @GetMapping("/routine-date/{routineDateId}")
    public ResponseEntity<PerformedRoutineResponse> findById(
            @SessionAttribute("member") Member member,
            @PathVariable("routineDateId") Long routineDateId
    ) {
        PerformedRoutineResponse performedRoutineResponse = performedRoutineService.findById(routineDateId);
        return ResponseEntity.ok(performedRoutineResponse);
    }

    @GetMapping("/routine-date")
    public ResponseEntity<PerformedRoutineResponse> findByRoutineDate(
            @SessionAttribute("member") Member member,
            @RequestParam("routineDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate routineDate
    ) {
        return performedRoutineService.findByMemberIdAndPerformedDate(member.getId(), routineDate)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/routines/{routineDateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("routineDateId") Long routineDateId
    ) {
        performedRoutineService.delete(routineDateId);
    }

    @PatchMapping("/performedRoutine/start/{performedRoutineId}")
    @ResponseStatus(HttpStatus.OK)
    public void startExercise(@PathVariable("performedRoutineId") Long performedRoutineId) {
        performedRoutineService.startExercise(performedRoutineId);
    }

    @PatchMapping("/performedRoutine/end/{performedRoutineId}")
    @ResponseStatus(HttpStatus.OK)
    public void endExercise(
            @PathVariable("performedRoutineId") Long performedRoutineId,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime
    ) {
        performedRoutineService.endExercise(performedRoutineId, endTime);
    }
}
