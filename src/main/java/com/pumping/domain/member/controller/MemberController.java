package com.pumping.domain.member.controller;

import com.pumping.domain.member.dto.DeleteMemberRequest;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;
import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping(value = "/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete
            (
                    @RequestBody DeleteMemberRequest deleteMemberRequest,
                    @AuthenticationPrincipal Member member
            ) {
        memberService.delete(deleteMemberRequest.getPassword(), member);
    }

    @GetMapping(value = "/members/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendEmail
            (
                    @RequestParam("email") String email
            ) {
        memberService.sendCodeEmail(email);
    }

    @PostMapping(value = "/members/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkEmailCode
            (
                    @RequestBody EmailCodeCheckRequest emailCodeCheckRequest
            ) {
        memberService.checkCode(emailCodeCheckRequest);
    }

}
