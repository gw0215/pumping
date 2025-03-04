package com.pumping.domain.routine.controller;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.dto.RoutineExerciseRequests;
import com.pumping.domain.routine.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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


}
