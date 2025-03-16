package com.pumping.domain.routinedate.controller;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.dto.RoutineDetailResponse;
import com.pumping.domain.routinedate.dto.RoutineDateRequest;
import com.pumping.domain.routinedate.service.RoutineDateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoutineDateController {

    private final RoutineDateService routineDateService;


    @PostMapping("/routine-date")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRoutineDate(
            @RequestBody RoutineDateRequest routineDateRequest
    ) {
        routineDateService.save(routineDateRequest.getRoutineId(), routineDateRequest.getPerformedDate());
    }

    @GetMapping("/routine-date")
    public ResponseEntity<RoutineDetailResponse> findByRoutineDate(
            @AuthenticationPrincipal Member member,
            @RequestParam("routineDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate routineDate
    ) {
        RoutineDetailResponse response = routineDateService.findByMemberIdAndPerformedDate(member.getId(), routineDate);

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }
}
