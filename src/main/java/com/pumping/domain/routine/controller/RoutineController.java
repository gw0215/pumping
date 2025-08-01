package com.pumping.domain.routine.controller;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.dto.RoutineDetailResponse;
import com.pumping.domain.routine.dto.RoutineExerciseRequests;
import com.pumping.domain.routine.dto.RoutineResponse;
import com.pumping.domain.routine.service.RoutineService;
import com.pumping.global.common.annotation.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;

    @PostMapping("/routines")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(
            @AuthMember Member member,
            @RequestBody RoutineExerciseRequests routineExerciseRequests
    ) {
        routineService.create(member, routineExerciseRequests);
    }

    @GetMapping("/routines")
    @ResponseStatus(HttpStatus.OK)
    public List<RoutineResponse> findAll(
            @AuthMember Member member
    ) {
        return routineService.findAll(member.getId());
    }

    @GetMapping("/routines/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoutineDetailResponse findById(
            @PathVariable("id") Long id
    ) {
        return routineService.findById(id);
    }



}
