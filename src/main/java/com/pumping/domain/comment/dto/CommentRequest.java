package com.pumping.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequest {

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    @Size(max = 50, message = "댓글은 50자 이내여야 합니다.")
    private String comment;

    public CommentRequest(String comment) {
        this.comment = comment;
    }
}
