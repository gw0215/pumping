package com.pumping.domain.comment.controller;

import com.pumping.domain.comment.dto.CommentRequest;
import com.pumping.domain.comment.dto.CommentResponse;
import com.pumping.domain.comment.service.CommentService;
import com.pumping.domain.member.model.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("boards/{boardId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> save(
            @SessionAttribute("member") Member member,
            @PathVariable("boardId") Long boardId,
            @Valid @RequestBody CommentRequest commentRequest
    ) {
        Long id = commentService.save(member, boardId, commentRequest.getComment());
        URI location = URI.create("/boards/" + boardId + "/comments/" + id);
        return ResponseEntity.created(location).build();
    }

    @GetMapping("boards/{boardId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public Page<CommentResponse> findAll(
            @PathVariable("boardId") Long boardId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return commentService.findAll(boardId,pageable);
    }

    @PatchMapping("boards/{boardId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @SessionAttribute("member") Member member,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentRequest commentRequest
    ) {
        commentService.update(commentId, commentRequest.getComment(),member);
    }

    @DeleteMapping("boards/{boardId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @SessionAttribute("member") Member member,
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId
    ) {
        commentService.delete(boardId, commentId, member);
    }
}
