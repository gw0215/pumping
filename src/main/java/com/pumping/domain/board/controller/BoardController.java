package com.pumping.domain.board.controller;

import com.pumping.domain.board.dto.BoardRequest;
import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.service.BoardService;
import com.pumping.domain.media.service.MediaService;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final MediaService mediaService;

    @PostMapping(value = "/boards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @AuthenticationPrincipal Member member,
            @RequestPart("board") BoardRequest boardRequest,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        Long boardId = boardService.save(member, boardRequest.getTitle(), boardRequest.getContent());

        if (file != null && !file.isEmpty()) {
            mediaService.save(boardId, file);
        }
    }

    @GetMapping(value = "/boards")
    @ResponseStatus(HttpStatus.OK)
    public List<BoardResponse> findAll() {
        return boardService.findALl();
    }

}
