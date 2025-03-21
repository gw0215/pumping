package com.pumping.domain.board.dto;

import com.pumping.domain.media.dto.MediaResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardResponse {

    private Long id;

    private String memberNickname;

    private String title;

    private String content;

    private Integer likeCount;

    private List<MediaResponse> mediaResponses;

    private boolean liked;

    public BoardResponse(Long id, String memberNickname, String title, String content, Integer likeCount, List<MediaResponse> mediaResponses, boolean liked) {
        this.id = id;
        this.memberNickname = memberNickname;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.mediaResponses = mediaResponses;
        this.liked = liked;
    }
}
