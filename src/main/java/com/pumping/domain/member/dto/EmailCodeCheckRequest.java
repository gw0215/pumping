package com.pumping.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailCodeCheckRequest {

    private String email;

    private String code;

    public EmailCodeCheckRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

}
