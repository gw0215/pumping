package com.pumping.domain.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardRequest {

    private String title;

    private String content;

    public BoardRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
