package com.pumping.domain.board.controller;

import com.pumping.domain.board.dto.BoardRequest;
import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.service.BoardService;
import org.springframework.data.domain.Page;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping(value = "/boards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @AuthenticationPrincipal Member member,
            @RequestPart("board") BoardRequest boardRequest,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        boardService.save(member, boardRequest.getTitle(), boardRequest.getContent(), file);
    }

    @GetMapping(value = "/boards")
    @ResponseStatus(HttpStatus.OK)
    public Page<BoardResponse> findAll(
            @AuthenticationPrincipal Member member,
            @PageableDefault Pageable pageable
    ) {
        return boardService.findAll(member, pageable);
    }

    @PatchMapping(value = "/boards/{boardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal Member member,
            @RequestBody BoardRequest boardRequest
    ) {
        boardService.update(member, boardId, boardRequest.getTitle(), boardRequest.getContent());
    }

    @DeleteMapping(value = "/boards/{boardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("boardId") Long boardId
    ) {
        boardService.deleteById(boardId);
    }


}
