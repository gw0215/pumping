package com.pumping.domain.board.dto;

import com.pumping.domain.media.dto.MediaResponse;

import java.util.List;

public record BoardResponse(Long boardId, Long memberId, String memberNickname, String profileImagePath, String title, String content,
                            Integer likeCount, Integer commentCount,
                            List<MediaResponse> mediaResponses, boolean liked) {

}
