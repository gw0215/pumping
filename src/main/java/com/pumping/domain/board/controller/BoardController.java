package com.pumping.domain.board.controller;

import com.pumping.domain.board.dto.BoardRequest;
import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.service.BoardService;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping(value = "/boards")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @AuthenticationPrincipal Member member,
            @RequestBody BoardRequest boardRequest
    ) {
        boardService.save(member, boardRequest.getContent());
    }

    @GetMapping(value = "/boards")
    @ResponseStatus(HttpStatus.OK)
    public List<BoardResponse> findAll() {
        return boardService.findALl();
    }

}
