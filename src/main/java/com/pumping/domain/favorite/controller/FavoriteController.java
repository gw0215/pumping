package com.pumping.domain.favorite.controller;

import com.pumping.domain.favorite.service.FavoriteService;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping(value = "/boards/{boardId}/favorite")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @AuthenticationPrincipal Member member,
            @PathVariable("boardId") Long boardId
    ) {
        favoriteService.save(member, boardId);
    }

    @DeleteMapping(value = "/boards/{boardId}/favorite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal Member member,
            @PathVariable("boardId") Long boardId
    ) {
        favoriteService.delete(member, boardId);
    }


}
