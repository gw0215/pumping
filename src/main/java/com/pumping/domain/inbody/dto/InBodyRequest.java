package com.pumping.domain.inbody.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class InBodyRequest {

    @NotNull(message = "체중은 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "체중은 0보다 커야 합니다.")
    private Float weight;

    @NotNull(message = "골격근량은 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "골격근량은 0보다 커야 합니다.")
    private Float smm;

    @NotNull(message = "체지방량은 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "체지방량은 0보다 커야 합니다.")
    private Float bfm;

    @NotNull(message = "날짜는 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    public InBodyRequest(Float weight, Float smm, Float bfm, LocalDate date) {
        this.weight = weight;
        this.smm = smm;
        this.bfm = bfm;
        this.date = date;
    }
}
