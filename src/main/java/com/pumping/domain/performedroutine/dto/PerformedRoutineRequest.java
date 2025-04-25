package com.pumping.domain.performedroutine.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PerformedRoutineRequest {

    private Long routineId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate performedDate;

    public PerformedRoutineRequest(Long routineId, LocalDate performedDate) {
        this.routineId = routineId;
        this.performedDate = performedDate;
    }
}