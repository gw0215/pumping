package com.pumping.domain.board.dto;

import com.pumping.domain.media.dto.MediaResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardResponse {

    private Long id;

    private String title;

    private String content;

    private Integer likeCount;

    private List<MediaResponse> mediaResponses;

    public BoardResponse(Long id, String title, String content, Integer likeCount, List<MediaResponse> mediaResponses) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.mediaResponses = mediaResponses;
    }
}
