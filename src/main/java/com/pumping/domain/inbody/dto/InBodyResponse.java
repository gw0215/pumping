package com.pumping.domain.inbody.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record InBodyResponse(Float weight, Float smm, Float bfm,@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate date) {
}
