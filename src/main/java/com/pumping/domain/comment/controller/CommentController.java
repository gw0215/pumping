package com.pumping.domain.comment.controller;

import com.pumping.domain.comment.dto.CommentRequest;
import com.pumping.domain.comment.dto.CommentResponse;
import com.pumping.domain.comment.service.CommentService;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("boards/{boardId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @AuthenticationPrincipal Member member,
            @PathVariable("boardId") Long boardId,
            @RequestBody CommentRequest commentRequest
    ) {
        commentService.save(member, boardId, commentRequest.getComment());
    }

    @GetMapping("boards/{boardId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> findAll(
            @PathVariable("boardId") Long boardId
    ) {
        return commentService.findAll(boardId);
    }

    @DeleteMapping("boards/{boardId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public void delete(
            @PathVariable("boardId") Long boardId
    ) {
        commentService.delete(boardId);
    }
}
