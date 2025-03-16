package com.pumping.domain.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardResponse {

    private Long id;

    private String content;

    private Integer likeCount;

    public BoardResponse(Long id, String content, Integer likeCount) {
        this.id = id;
        this.content = content;
        this.likeCount = likeCount;
    }
}
