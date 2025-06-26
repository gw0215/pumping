package com.pumping.domain.emailverification.controller;

import com.pumping.domain.emailverification.service.EmailVerificationService;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;


    @GetMapping(value = "/emails")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendCode
            (
                    @RequestParam("email") String email
            ) {
        emailVerificationService.sendCode(email);
    }

    @PostMapping(value = "/emails")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkCode
            (
                    @Valid @RequestBody EmailCodeCheckRequest emailCodeCheckRequest
            ) {
        emailVerificationService.checkCode(emailCodeCheckRequest);
    }


}
