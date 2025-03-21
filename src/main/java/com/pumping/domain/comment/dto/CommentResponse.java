package com.pumping.domain.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponse {

    private Long id;

    private String comment;

    public CommentResponse(Long id, String comment) {
        this.id = id;
        this.comment = comment;
    }
}
