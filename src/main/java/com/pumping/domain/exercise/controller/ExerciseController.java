package com.pumping.domain.exercise.controller;

import com.pumping.domain.exercise.dto.ExerciseResponse;
import com.pumping.domain.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExerciseController {
    
    private final ExerciseService exerciseService;

    @GetMapping(value = "/exercises")
    @ResponseStatus(HttpStatus.OK)
    public List<ExerciseResponse> findAll(
            @RequestParam(value = "part", required = false) String part
    ) {
        return exerciseService.findAll(part);
    }

}
