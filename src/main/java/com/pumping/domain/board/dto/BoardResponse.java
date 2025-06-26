package com.pumping.domain.board.dto;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.media.dto.MediaResponse;
import com.pumping.domain.member.model.Member;

import java.util.List;

public record BoardResponse(
        Long boardId,
        Long memberId,
        String memberNickname,
        String profileImagePath,
        String title,
        String content,
        Integer likeCount,
        Integer commentCount,
        List<MediaResponse> mediaResponses,
        boolean liked
) {
    public static BoardResponse from(Board board, boolean liked) {
        List<MediaResponse> mediaResponses = board.getMediaList().stream()
                .map(media -> new MediaResponse(media.getId(), media.getFileName(), media.getFileType()))
                .toList();

        return new BoardResponse(
                board.getId(),
                board.getMember().getId(),
                board.getMember().getNickname(),
                board.getMember().getProfileImagePath(),
                board.getTitle(),
                board.getContent(),
                board.getLikeCount(),
                board.getCommentCount(),
                mediaResponses,
                liked
        );
    }
}