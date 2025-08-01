package com.pumping.domain.member.controller;

import com.pumping.domain.member.dto.*;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.service.MemberService;
import com.pumping.global.common.annotation.AuthMember;
import com.pumping.global.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping(value = "/members")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody MemberSignUpRequest memberSignUpRequest) {
        memberService.save(memberSignUpRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {
        Member member = memberService.login(loginRequest.getEmail(), loginRequest.getPassword());
        String token = jwtUtil.generateToken(member);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();
    }

//    @PostMapping("/logout")
//    public void logout(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            session.invalidate();
//        }
//    }

    @GetMapping("/members/profile")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse getProfile(@AuthMember Member member) {
        return new MemberResponse(member.getNickname(), member.getEmail(), member.getProfileImagePath());
    }

    @DeleteMapping(value = "/members")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@AuthMember  Member member) {
        memberService.delete(member.getId());
    }

    @PatchMapping(value = "/members/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfileImage(@AuthMember  Member member, @RequestPart(value = "file", required = false) MultipartFile file) {
        memberService.updateProfileImage(member.getId(), file);
    }

    @PostMapping("/verify-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyPassword(@AuthMember  Member member, @Valid @RequestBody VerifyPasswordRequest request) {
        memberService.verifyPassword(member, request.getPassword());
    }

    @GetMapping("/verify-email")
    @ResponseStatus(HttpStatus.OK)
    public void checkDuplicationEmail(@RequestParam(value = "email") String email) {
        memberService.checkDuplicationEmail(email);
    }

    @PostMapping("/fcm-token")
    public void saveFcmToken(@Valid @RequestBody FcmTokenRequest request, @AuthMember Member member) {
        memberService.saveFcmToken(member.getId(), request.getFcmToken());
    }

}
