package com.pumping.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyPasswordRequest {

    private String password;

    public VerifyPasswordRequest(String password) {
        this.password = password;
    }

}
