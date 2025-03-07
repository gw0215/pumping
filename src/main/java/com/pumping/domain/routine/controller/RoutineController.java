package com.pumping.domain.routine.controller;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.dto.RoutineExerciseRequests;
import com.pumping.domain.routine.dto.RoutineResponse;
import com.pumping.domain.routine.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;

    @PostMapping("/routines")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRoutine(
            @AuthenticationPrincipal Member member,
            @RequestBody RoutineExerciseRequests routineExerciseRequests
    ) {
        routineService.create(member, routineExerciseRequests);
    }

    @GetMapping("/routines")
    @ResponseStatus(HttpStatus.OK)
    public Page<RoutineResponse> findAllRoutines(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal Member member
    ) {
        return routineService.findAll(member.getId(), pageable);
    }


}
