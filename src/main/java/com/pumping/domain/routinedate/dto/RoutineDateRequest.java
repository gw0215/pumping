package com.pumping.domain.routinedate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class RoutineDateRequest {

    private Long routineId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate performedDate;

    public RoutineDateRequest(Long routineId, LocalDate performedDate) {
        this.routineId = routineId;
        this.performedDate = performedDate;
    }
}