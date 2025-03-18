package com.pumping.domain.memo.controller;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.memo.dto.MemoRequest;
import com.pumping.domain.memo.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping("/memo")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @AuthenticationPrincipal Member member,
            @RequestBody MemoRequest memoRequest
    ) {
        memoService.save(member, memoRequest.getExerciseId(), memoRequest.getDetail());
    }

}
