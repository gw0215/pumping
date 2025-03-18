package com.pumping.domain.memo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoRequest {

    private Long exerciseId;

    private String detail;

    public MemoRequest(Long exerciseId, String detail) {
        this.exerciseId = exerciseId;
        this.detail = detail;
    }
}
