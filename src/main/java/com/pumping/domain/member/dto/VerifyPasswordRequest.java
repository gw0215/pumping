package com.pumping.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyPasswordRequest {

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    public VerifyPasswordRequest(String password) {
        this.password = password;
    }
}