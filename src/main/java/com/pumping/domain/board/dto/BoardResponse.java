package com.pumping.domain.board.dto;

import com.pumping.domain.media.dto.MediaResponse;
import lombok.Getter;

import java.util.List;

public record BoardResponse(Long id, String memberNickname, String title, String content, Integer likeCount,
                            List<MediaResponse> mediaResponses, boolean liked) {

}
