package com.pumping.domain.member.controller;

import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping(value = "/members")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp
            (
                    @RequestBody MemberSignUpRequest memberSignUpRequest
            ) {
        memberService.save(memberSignUpRequest);
    }


}
