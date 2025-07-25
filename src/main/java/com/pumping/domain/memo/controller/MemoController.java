package com.pumping.domain.memo.controller;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.memo.dto.MemoRequest;
import com.pumping.domain.memo.service.MemoService;
import com.pumping.global.common.annotation.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping("/memo")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @AuthMember Member member,
            @RequestBody MemoRequest memoRequest
    ) {
        memoService.save(member, memoRequest.getExerciseId(), memoRequest.getDetail());
    }

}
