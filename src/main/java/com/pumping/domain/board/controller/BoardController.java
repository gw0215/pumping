package com.pumping.domain.board.controller;

import com.pumping.domain.board.dto.BoardRequest;
import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping(value = "/boards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> save(
            @SessionAttribute("member") Member member,
            @Valid @RequestPart("board") BoardRequest boardRequest,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        Long id = boardService.save(member, boardRequest.getTitle(), boardRequest.getContent(), file);
        URI location = URI.create("/boards/" + id);
        return ResponseEntity.created(location).build();
    }

    @GetMapping(value = "/boards")
    @ResponseStatus(HttpStatus.OK)
    public Page<BoardResponse> findAll(
            @SessionAttribute("member") Member member,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return boardService.findAll(member, pageable);
    }

    @PatchMapping(value = "/boards/{boardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @SessionAttribute("member") Member member,
            @PathVariable("boardId") Long boardId,
            @Valid @RequestBody BoardRequest boardRequest
    ) {
        boardService.update(boardId, boardRequest.getTitle(), boardRequest.getContent(), member);
    }

    @DeleteMapping(value = "/boards/{boardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @SessionAttribute("member") Member member,
            @PathVariable("boardId") Long boardId
    ) {
        boardService.deleteById(boardId, member);
    }


}
