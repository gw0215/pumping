package com.pumping.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailCodeCheckRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "인증 코드는 필수입니다.")
    @Size(min = 5, max = 5, message = "인증 코드는 5자리여야 합니다.")
    private String code;

    public EmailCodeCheckRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }
}