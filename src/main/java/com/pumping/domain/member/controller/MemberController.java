package com.pumping.domain.member.controller;

import com.pumping.domain.member.dto.LoginRequest;
import com.pumping.domain.member.dto.MemberResponse;
import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.dto.VerifyPasswordRequest;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping(value = "/members")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody MemberSignUpRequest memberSignUpRequest) {
        memberService.save(memberSignUpRequest);
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Member member = memberService.login(loginRequest.getEmail(), loginRequest.getPassword());
        HttpSession session = request.getSession();
        session.setAttribute("member", member);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @GetMapping(value = "/members/profile")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse getProfile(@SessionAttribute("member") Member member) {
        return new MemberResponse(member.getNickname(), member.getEmail(), member.getProfileImagePath());
    }

    @DeleteMapping(value = "/members")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@SessionAttribute("member") Member member) {
        memberService.delete(member.getId());
    }

    @PatchMapping(value = "/members/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfileImage(@SessionAttribute("member") Member member, @RequestPart(value = "file", required = false) MultipartFile file) {
        memberService.updateProfileImage(member.getId(), file);
    }

    @PostMapping("/verify-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyPassword(@SessionAttribute("member") Member member, @RequestBody VerifyPasswordRequest request) {
        memberService.verifyPassword(member, request.getPassword());
    }

    @GetMapping("/verify-email")
    @ResponseStatus(HttpStatus.OK)
    public void duplicateEmail(@RequestParam(value = "email") String email) {
        memberService.checkDuplicationEmail(email);
    }


}
