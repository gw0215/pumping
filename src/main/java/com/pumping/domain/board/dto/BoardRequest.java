package com.pumping.domain.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardRequest {

    private String content;

    public BoardRequest(String content) {
        this.content = content;
    }
}
