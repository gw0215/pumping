package com.pumping.domain.member.controller;

import com.pumping.domain.member.dto.MemberResponse;
import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.dto.VerifyPasswordRequest;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping(value = "/members/profile")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse getProfile
            (
                    @AuthenticationPrincipal Member member
            ) {

        return new MemberResponse(member.getNickname(), member.getEmail());
    }

    @DeleteMapping(value = "/members")
    @ResponseStatus(HttpStatus.OK)
    public void delete
            (
                    @AuthenticationPrincipal Member member
            ) {
        memberService.delete(member.getId());
    }


    @GetMapping(value = "/members/profile-image")
    @ResponseStatus(HttpStatus.OK)
    public byte[] getProfileImage
            (
                    @AuthenticationPrincipal Member member
            ) {
        return memberService.getProfileImage(member.getId());
    }

    @PatchMapping(value = "/members/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfileImage
            (
                    @AuthenticationPrincipal Member member,
                    @RequestPart(value = "file", required = false) MultipartFile file
            ) {
        memberService.updateProfileImage(member.getId(), file);
    }

    @PostMapping("/verify-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyPassword(
            @RequestBody VerifyPasswordRequest request,
            @AuthenticationPrincipal Member member
    ) {
        memberService.verifyPassword(member, request.getPassword());
    }


}
