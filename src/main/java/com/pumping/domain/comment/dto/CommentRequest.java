package com.pumping.domain.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequest {

    private String comment;

    public CommentRequest(String comment) {
        this.comment = comment;
    }
}
