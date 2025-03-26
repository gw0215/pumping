package com.pumping.domain.emailverification.controller;

import com.pumping.domain.emailverification.service.EmailVerificationService;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;


    @GetMapping(value = "/email")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendEmail
            (
                    @RequestParam("email") String email
            ) {
        emailVerificationService.sendCodeEmail(email);
    }

    @PostMapping(value = "/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkEmailCode
            (
                    @RequestBody EmailCodeCheckRequest emailCodeCheckRequest
            ) {
        emailVerificationService.checkCode(emailCodeCheckRequest);
    }


}
