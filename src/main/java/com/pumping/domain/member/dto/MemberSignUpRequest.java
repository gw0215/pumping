package com.pumping.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignUpRequest {

    private String password;

    private String email;

    private String nickname;

    public MemberSignUpRequest(String password, String email, String nickname) {
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }
}
