package com.pumping.domain.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponse {

    private Long id;

    private String memberNickname;

    private String content;

    public CommentResponse(Long id, String memberNickname, String content) {
        this.id = id;
        this.memberNickname = memberNickname;
        this.content = content;
    }
}
