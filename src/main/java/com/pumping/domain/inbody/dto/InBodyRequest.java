package com.pumping.domain.inbody.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class InBodyRequest {

    private Float weight;

    private Float smm;

    private Float bfm;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    public InBodyRequest(Float weight, Float smm, Float bfm, LocalDate date) {
        this.weight = weight;
        this.smm = smm;
        this.bfm = bfm;
        this.date = date;
    }
}
