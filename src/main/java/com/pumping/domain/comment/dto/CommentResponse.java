package com.pumping.domain.comment.dto;


public record CommentResponse(Long commentId, Long memberId, String memberNickname, String content) { }
